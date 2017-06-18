package com.tresw.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tresw.stock.domain.Product;

/**
 * This class is the data base access layer for products
 * @author alejandro
 *
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

	/**
	 * Finds the product by it's serial number
	 * @param serial
	 * @return null if if does not exist, an instance of product if it exists
	 */
	@Query("SELECT p FROM Product p WHERE p.serialNo = :serial")
	public Product findBySerialNo(@Param("serial") String serial);
}