package com.tresw.stock.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.tresw.stock.domain.Stock;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class StockControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	private MockMvc mockMvc;

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	//Tests rest call to sell a product when it does not exist
	@Test
	public void sellProductNotFound() throws Exception {
		mockMvc.perform(put("/product/sell/34")).andExpect(status().isNotFound());
	}

	//Tests rest call to sell successfully
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void sellProduct() throws Exception {
		mockMvc.perform(put("/product/sell/1")).andExpect(status().isOk());
	}

	//Tests rest call to get a product
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void getProduct() throws Exception {
		mockMvc.perform(get("/product/1")).andExpect(status().isOk());
	}

	//Tests rest call to get all products from a stock
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void getProducts() throws Exception {
		mockMvc.perform(get("/stock/1/products")).andExpect(status().isOk());
	}

	//Tests rest call to get all products from a stock which does not exist
	@Test
	public void getProductsStockNotFound() throws Exception {
		mockMvc.perform(get("/stock/7/products")).andExpect(status().isNotFound());
	}

	//Tests rest call to reserve a product that does not exist
	@Test
	public void reserveProductNotFound() throws Exception {
		mockMvc.perform(put("/product/reserve/34")).andExpect(status().isNotFound());
	}

	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void unReserveProduct() throws Exception {
		mockMvc.perform(put("/product/reserve/5")).andExpect(status().isNotFound());
	}
	
	//Tests rest call to reserve a product
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void reserveProduct() throws Exception {
		mockMvc.perform(put("/product/reserve/1")).andExpect(status().isOk());
	}

	//Tests rest call add a product to a stock
	@Test
	public void addProduct() throws Exception {
		mockMvc.perform(put("/stock/1/300")).andExpect(status().isOk());
	}

	//Tests rest call add a product to a stock when the stock does not exist
	@Test
	public void addProductStockNotExists() throws Exception {
		mockMvc.perform(put("/stock/11/300")).andExpect(status().isNotFound());
	}

	//Tests rest call add a product to a stock when the [product already exists
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void addProductProductAlreadyExists() throws Exception {
		mockMvc.perform(put("/stock/1/1")).andExpect(status().isConflict());
	}

	//Tests rest call create a stock
	@Test
	public void addStock() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Stock obj = new Stock();

		obj.setLongDescription("test");
		obj.setName("test");
		obj.setPrice(new BigDecimal(10));
		obj.setQuantity(0);
		obj.setShortDescription("test");

		// Object to JSON in String
		String jsonInString = mapper.writeValueAsString(obj);

		mockMvc.perform(put("/stock/").contentType(MediaType.APPLICATION_JSON).content(jsonInString))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.*", hasSize(7)))
				.andExpect(jsonPath("$.longDescription", is(obj.getLongDescription())))
				.andExpect(jsonPath("$.name", is(obj.getName())))
				.andExpect(jsonPath("$.quantity", is(obj.getQuantity())))
				.andExpect(jsonPath("$.shortDescription", is(obj.getShortDescription())))
				.andExpect(jsonPath("$.price", is(obj.getPrice().intValue())))
				.andExpect(jsonPath("$.products.*", hasSize(0)));
	}
	
	//Tests rest call update a stock
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void updateStock() throws Exception {
		

		MvcResult result = mockMvc.perform(get("/stock/1")).andReturn();
		String content = result.getResponse().getContentAsString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		Stock s = mapper.readValue(content, Stock.class);
		
		s.setLongDescription("test 1");
		
		// Object to JSON in String
		String jsonInString = mapper.writeValueAsString(s);

		mockMvc.perform(put("/stock/1").contentType(MediaType.APPLICATION_JSON).content(jsonInString))
				.andExpect(status().isOk()).andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$.*", hasSize(7)))
				.andExpect(jsonPath("$.longDescription", is(s.getLongDescription())))
				.andExpect(jsonPath("$.name", is(s.getName())))
				.andExpect(jsonPath("$.quantity", is(s.getQuantity())))
				.andExpect(jsonPath("$.shortDescription", is(s.getShortDescription())))
				.andExpect(jsonPath("$.products.*", hasSize(10)));
	}
	
	//Tests rest call update a stock when the data is incorrect, stock id does not match the product's
	//stock if
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void updateStockConflict() throws Exception {
		

		MvcResult result = mockMvc.perform(get("/stock/1")).andReturn();
		String content = result.getResponse().getContentAsString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		Stock s = mapper.readValue(content, Stock.class);
		
		s.setLongDescription("test 1");
		
		// Object to JSON in String
		String jsonInString = mapper.writeValueAsString(s);

		mockMvc.perform(put("/stock/2").contentType(MediaType.APPLICATION_JSON).content(jsonInString))
				.andExpect(status().isConflict());
	}
	
	//Tests rest call update a stock when the data is incorrect, not all mandatory fields are sent
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void updateStockNull() throws Exception {
		

		MvcResult result = mockMvc.perform(get("/stock/1")).andReturn();
		String content = result.getResponse().getContentAsString();
		
		ObjectMapper mapper = new ObjectMapper();
		
		Stock s = mapper.readValue(content, Stock.class);
		

		s.setLongDescription(null);

		s.setShortDescription(null);
		
		// Object to JSON in String
		String jsonInString = mapper.writeValueAsString(s);

		mockMvc.perform(put("/stock/2").contentType(MediaType.APPLICATION_JSON).content(jsonInString))
				.andExpect(status().isBadRequest());
	}

	//Tests rest call to get a stock
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void getStock() throws Exception {
		mockMvc.perform(get("/stock/1")).andExpect(status().isOk());
	}

	//Tests rest call to get a stock and it does not exist
	@Test
	public void getStockNotExist() throws Exception {
		mockMvc.perform(get("/stock/111")).andExpect(status().isNotFound());
	}

}
