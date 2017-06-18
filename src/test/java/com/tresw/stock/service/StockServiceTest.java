package com.tresw.stock.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.tresw.stock.domain.Product;
import com.tresw.stock.domain.Stock;
import com.tresw.stock.domain.Product.Status;
import com.tresw.stock.service.StockService.ProductAlreadyExistsException;
import com.tresw.stock.service.StockService.ProductNotAvailableException;
import com.tresw.stock.service.StockService.ProductNotFoundException;
import com.tresw.stock.service.StockService.StockNotFoundException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
public class StockServiceTest {

	@Autowired
	private StockService stockService;

	//Tests add product when a product with the serial number already exists
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	@Test(expected = ProductAlreadyExistsException.class)
	public void addProductAlreadyExists() {
		stockService.addProduct(1, "1");
	}

	//Tests add product when a product with the stock to which it is going to be added
	//does not exist
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	@Test(expected = StockNotFoundException.class)
	public void addProductStockNotExist() {
		stockService.addProduct(3, "1");
	}

	//Tests add product and everything goes ok
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void addProductOK() {
		stockService.addProduct(1, "32");
		Assert.assertNotNull(stockService.getProduct("32"));
	}

	//Tests creating several products at once
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void addProductsOK() {
		List<String> serials = Arrays.asList("13", "14");
		stockService.addProducts(1, serials);
	}


	//Tests creating a new stock and everything goes ok
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void addStockOK() {
		Stock s = new Stock();
		s.setLongDescription("A test stock");
		s.setName("test stock");
		s.setPrice(new BigDecimal(10));
		s.setQuantity(1);
		s.setShortDescription("test");
		stockService.addStock(s);
	}

	//Tests updating a stock and everything goes ok
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void updateStock() {
		Stock s = stockService.getStock(1);
		String nameBefore = s.getName();
		s.setName("test");
		stockService.updateStock(1,s);
		s = stockService.getStock(1);
		Assert.assertNotEquals(nameBefore, s.getName());
	}
	
	//Tests getting a product
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void getProduct() {
		Assert.assertNotNull(stockService.getProduct("10"));
	}
	
	//Tests getting all products from a stock
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void getProducts() {
		Assert.assertEquals(stockService.getProducts(1).size(), 10);
	}

	//Tests getting a stocks paginated
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void getStocks() {
		PageRequest pageRequest = new PageRequest(1, 10, new Sort(Sort.Direction.DESC, "name"));
		List<Stock> stocks = stockService.getStocks(pageRequest).getContent();
		for (int i = 0; i < stocks.size() - 1; i++) {
			Stock s1 = stocks.get(i);
			Stock s2 = stocks.get(i + 1);
			Assert.assertTrue(s1.getName().compareTo(s2.getName()) > 0);
		}
	}

	//Tests getting a stock
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void getStock() {
		Assert.assertNotNull(stockService.getStock(1));
	}

	//Tests reserving a product that does not exist
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	@Test(expected = ProductNotFoundException.class)
	public void reserveProductNotExists() {
		stockService.reserveProduct("123");
	}
	
	//Tests undoing a reservation a product that does not exist
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void unReserveProduct() {
		Product p = stockService.getProduct("5");
		int quantityBefore = stockService.getProduct("1").getStock().getQuantity();
		Assert.assertEquals(Status.RESERVED, p.getStatus());
		stockService.unReserveProduct("5");
		p = stockService.getProduct("5");
		int quantityAfter = stockService.getProduct("1").getStock().getQuantity();
		Assert.assertEquals(Status.IN_STOCK, p.getStatus());
		Assert.assertNotEquals(quantityBefore, quantityAfter);
	}

	//Tests reserving a product when it is not available, status is not in_stock
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	@Test(expected = ProductNotAvailableException.class)
	public void reserveProductNotAvailable() {
		stockService.reserveProduct("6");
	}

	//Tests reserving a product successfully
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void reserveProductOK() {
		int quantityBefore = stockService.getProduct("1").getStock().getQuantity();
		stockService.reserveProduct("1");
		int quantityAfter = stockService.getProduct("1").getStock().getQuantity();
		Assert.assertNotEquals(quantityBefore, quantityAfter);
	}

	//Tests selling a product successfully
	@Test
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	public void sellProductOK() {
		int quantityBefore = stockService.getProduct("2").getStock().getQuantity();
		stockService.sellProduct("2");
		Product p = stockService.getProduct("2");
		int quantityAfter = stockService.getProduct("2").getStock().getQuantity();
		Assert.assertNotEquals(quantityBefore, quantityAfter);
		Assert.assertEquals(Status.SOLD, p.getStatus());
	}

	//Tests selling a product that does not exist
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	@Test(expected = ProductNotFoundException.class)
	public void sellProductNotExists() {
		stockService.sellProduct("123");
	}
	
	//Tests selling a product when it is not available, status is not in_stock
	@DatabaseSetup(type = DatabaseOperation.CLEAN_INSERT, value = "/dataset.xml")
	@Test(expected = ProductNotAvailableException.class)
	public void sellProductNotAvailable() {
		stockService.reserveProduct("5");
	}
}
