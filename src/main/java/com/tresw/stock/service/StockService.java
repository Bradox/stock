package com.tresw.stock.service;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.tresw.stock.domain.Product;
import com.tresw.stock.domain.Product.Status;
import com.tresw.stock.domain.Stock;
import com.tresw.stock.repository.ProductRepository;
import com.tresw.stock.repository.StockRepository;

/**
 * This class implements all the necessary functions to manage the stock. It allows to create new stock, update stock as well
 * ass create, sell, reserve and unreserve products.
 *  
 * @author alejandro
 *
 */
@Service
public class StockService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private StockRepository stockRepository;
	
	/**
	 * Find stock by id
	 * @param stockId the stock's id
	 * @return stock instance
	 * @throws StockNotFoundException if the stock does not exist
	 */
	public Stock getStock(int stockId) {
		Stock stock = stockRepository.findOne(stockId);
		if (stock == null)
			throw new StockNotFoundException();
		return stock;
	}

	/**
	 * Manages all the different actions to sell a product.   
	 * @param serial the product's id
	 * @throws ProductNotFoundException if the product does not exist
	 * 		   ProductNotAvailableException if the product is not available for selling
	 */
	public void sellProduct(String serial) {
		Product product = getProduct(serial);
		if (!product.getStatus().equals(Status.IN_STOCK) && !product.getStatus().equals(Status.RESERVED)) {
			throw new ProductNotAvailableException();
		}
		Stock stock = product.getStock();
		product.setStatus(Status.SOLD);
		stock.setQuantity(stock.getQuantity() - 1); //decrease the product's quantity in the stock
		productRepository.save(product);
		stockRepository.save(stock);
	}

	/**
	 * Find product by id
	 * @param serial the product's serial number
	 * @return product instance
	 * @throws ProductNotFoundException if the product does not exist
	 */
	public Product getProduct(String serial) {
		Product product = productRepository.findBySerialNo(serial);
		if (product == null) {
			throw new ProductNotFoundException();
		}
		return product;
	}

	/**
	 * Gets the products associated to a stock
	 * @param idStock  the stocks id
	 * @return set of products
	 * @throws StockNotFoundException if the stock does not exist
	 */
	@Transactional
	public Set<Product> getProducts(int idStock) {
		Stock stock = getStock(idStock);
		Hibernate.initialize(stock.getProducts());
		return stock.getProducts();
	}

	/**
	 * Reserves a product
	 * @param serial the product's serial number
	 * @throws StockNotFoundException if the stock does not exist
	 * 		   ProductNotAvailableException if the product is not available for selling
	 */
	public void reserveProduct(String serial) {
		Product product = getProduct(serial);
		if (!product.getStatus().equals(Status.IN_STOCK)) {
			throw new ProductNotAvailableException();
		}
		Stock stock = product.getStock();
		product.setStatus(Status.RESERVED);
		stock.setQuantity(stock.getQuantity() - 1); //Decresases the number of available products
		productRepository.save(product);
		stockRepository.save(stock);
	}
	
	/**
	 * Undoes the product's reservation
	 * @param serial the product's serial number
	 * @throws StockNotFoundException if the stock does not exist
	 * 		   ProductNotAvailableException if the product is not available for unreserving
	 */
	public void unReserveProduct(String serial) {
		Product product = getProduct(serial);
		if (!product.getStatus().equals(Status.RESERVED)) {
			throw new ProductNotAvailableException();
		}
		Stock stock = product.getStock();
		product.setStatus(Status.IN_STOCK);
		stock.setQuantity(stock.getQuantity() + 1); //Increases the number of available products
		productRepository.save(product);
		stockRepository.save(stock);

	}

	/**
	 * Adds a product to the stock
	 * @param  stockId the stock's id to which the product is going to be added
	 * 		   serial the product's serial number to be added
	 * @throws StockNotFoundException if the stock does not exist
	 * 		   ProductAlreadyExistsException if the product already exists
	 */
	public Product addProduct(int stockId, String serial) {
		Stock stock = getStock(stockId);
		Product product = productRepository.findBySerialNo(serial);
		if (product != null) {
			throw new ProductAlreadyExistsException();
		}
		Product p = new Product();
		p.setSerialNo(serial);
		p.setStatus(Status.IN_STOCK);
		p.setStock(stock);
		productRepository.save(p);
		stock.setQuantity(stock.getQuantity() + 1); //Increases the number of available products
		stockRepository.save(stock);
		return product;
	}

	/**
	 * Adds all the products to the stock
	 * @param  stockId the stock's id to which the product is going to be added
	 * 		   serials list of serials of the products to be added
	 * @throws StockNotFoundException if the stock does not exist
	 * 		   ProductAlreadyExistsException if the product already exists
	 */
	public void addProducts(int stockId, List<String> serials) {
		for (String serial : serials) {
			addProduct(stockId, serial);
		}
	}

	/**
	 * Creates a new stock inventory
	 * @param  stock the stock to be created
	 */
	public Stock addStock(Stock stock) {
		return stockRepository.save(stock);
	}

	/**
	 * Updates the stock a new stock inventory
	 * @param  idStock the stock's id
	 * 		   stock the new stock information to be udpated
	 * @throws StockNotFoundException if the stock does not exist
	 * 		   IncorrectProductException if the data is incorrect
	 */
	public Stock updateStock(Integer idStock, Stock stock) {
		getStock(idStock);
		if(idStock==stock.getId()){
			return stockRepository.save(stock);
		}
		else {
			throw new IncorrectStockException();
		}
	}
	
	/**
	 * Returns a list of paginated stocks 
	 * @param  pageRequest containing the pagination details
	 */
	public Page<Stock> getStocks(PageRequest pageRequest) {
		return stockRepository.findAll(pageRequest);
	}

	@SuppressWarnings("serial")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	static class ProductNotFoundException extends RuntimeException {
	}

	@SuppressWarnings("serial")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	static class StockNotFoundException extends RuntimeException {
	}

	@SuppressWarnings("serial")
	@ResponseStatus(HttpStatus.CONFLICT)
	static class ProductAlreadyExistsException extends RuntimeException {
	}
	
	@SuppressWarnings("serial")
	@ResponseStatus(HttpStatus.CONFLICT)
	static class IncorrectStockException extends RuntimeException {
	}

	@SuppressWarnings("serial")
	@ResponseStatus(HttpStatus.CONFLICT)
	static class ProductNotAvailableException extends RuntimeException {
	}

}
