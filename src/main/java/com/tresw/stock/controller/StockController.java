package com.tresw.stock.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tresw.stock.domain.Stock;
import com.tresw.stock.service.StockService;

@RestController
public class StockController {

	@Autowired
	private StockService stockService;
	
	/**
	 * Manages the requests to sell a product
	 * @param serial the product's serial number
	 * @return HttpStatus.OK if everything goes ok
	 * 		   HttpStatus.NOT_FOUND if the product is not found
	 * 		   HttpStatus.CONFLICT if the product exists
	 */
	@RequestMapping(value = "/product/sell/{serial}", method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<?> sellProduct(@PathVariable String serial) {
		stockService.sellProduct(serial);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	
	/**
	 * Manages the requests to get a product
	 * @param serial the product's serial number
	 * @return HttpStatus.OK - The product
	 * 		   HttpStatus.NOT_FOUND if the product is not found
	 */
	@RequestMapping(value = "/product/{serial}", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<?> getProduct(@PathVariable String serial) {
		return ResponseEntity.ok(stockService.getProduct(serial));
	}

	/**
	 * Manages the requests to get all the product's from the same stock
	 * @param idstock the stock's id
	 * @return HttpStatus.OK - List of products
	 * 		   HttpStatus.NOT_FOUND if the stock is not found
	 */
	@RequestMapping(value = "/stock/{idstock}/products", method = RequestMethod.GET, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<?> getProducts(@PathVariable(name = "idstock") Integer idStock) {
		return ResponseEntity.ok(stockService.getProducts(idStock));
	}

	/**
	 * Manages the requests to reserve a product
	 * @param serial the product's serial number
	 * @return HttpStatus.OK - if everything goes ok
	 * 		   HttpStatus.NOT_FOUND if the stock is not found
	 */
	@RequestMapping(value = "/product/reserve/{serial}", method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<?> reserveProduct(@PathVariable String serial) {
		stockService.reserveProduct(serial);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	/**
	 * Manages the requests to unreserve a product
	 * @param serial the product's serial number
	 * @return HttpStatus.OK - if everything goes ok
	 * 		   HttpStatus.NOT_FOUND if the stock is not found
	 */
	@RequestMapping(value = "/product/unreserve/{serial}", method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<?> unReserveProduct(@PathVariable String serial) {
		stockService.unReserveProduct(serial);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Manages the requests to add a product to a stock
	 * @param  idstock stock to add the product too
	 *         serial the product's serial number
	 * @return HttpStatus.OK - if everything goes ok
	 * 		   HttpStatus.NOT_FOUND if the stock is not found
	 * 		   HttpStatus.CONFLICT if the product already exists
	 */
	@RequestMapping(value = "/stock/{idstock}/{serial}", method = RequestMethod.PUT, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	@ResponseBody
	public ResponseEntity<?> addProduct(@PathVariable(name = "idstock") Integer idStock,
			@PathVariable(name = "serial") String serial) {
		return ResponseEntity.ok(stockService.addProduct(idStock, serial));
	}

	/**
	 * Manages the requests to get a paginated list of products from a stock
	 * @param  page - page number we want to get, default 0
	 *         count - number of elements to return
	 * 		   direction - the sort order, defaulg ASC
	 * 		   sortProperty - property to sort by, default name
	 * @return HttpStatus.OK - if everything goes ok
	 * 		   HttpStatus.NOT_FOUND if the stock is not found
	 * 		   HttpStatus.CONFLICT if the product already exists
	 */
	@RequestMapping(value = "/stocks", method = RequestMethod.GET)
	public ResponseEntity<?> findAllStock(@RequestParam(value = "page", defaultValue = "0", required = false) int page,
			@RequestParam(value = "count", defaultValue = "10", required = false) int count,
			@RequestParam(value = "order", defaultValue = "ASC", required = false) Sort.Direction direction,
			@RequestParam(value = "sort", defaultValue = "name", required = false) String sortProperty) {
		Page<Stock> result = stockService.getStocks(new PageRequest(page, count, new Sort(direction, sortProperty)));
		return ResponseEntity.ok(result.getContent());
	}

	/**
	 * Manages the requests to create a new stock
	 * @param stock to be created
	 * @return stock created
	 */
	@RequestMapping(value = "/stock/",method = RequestMethod.PUT)
	public ResponseEntity<?> createStock(@Valid @RequestBody Stock stock) {
		return ResponseEntity.ok(stockService.addStock(stock));
	}
	
	/**
	 * Manages the requests to create a new stock
	 * @param  idStock the stocks id
	 * 		   stock to be updated
	 * @return stock created
	 * 		   HttpStatus.CONFLICT if data is incorrect
	 * 		   HttpStatus.NOT_FOUND if stock does not exist
	 */
	@RequestMapping(value = "/stock/{idStock}",method = RequestMethod.PUT)
	public ResponseEntity<?> updateStock(@PathVariable(name="idStock") Integer idStock, @Valid @RequestBody Stock stock) {
		return ResponseEntity.ok(stockService.updateStock(idStock, stock));
	}

	/**
	 * Manages the requests to get a given stock
	 * @param idStock the stocks id
	 * @return stock 
	 */
	@RequestMapping(value = "/stock/{idStock}", method = RequestMethod.GET)
	public ResponseEntity<?> find(@PathVariable(name="idStock") Integer idStock) {
		return ResponseEntity.ok(stockService.getStock(idStock));
	}

}