package com.tresw.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tresw.stock.domain.Stock;

/**
 * This class is the data base access layer for products
 * @author alejandro
 *
 */

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {
}