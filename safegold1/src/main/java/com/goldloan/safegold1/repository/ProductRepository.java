package com.goldloan.safegold1.repository;

import com.goldloan.safegold1.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByGramsBetween(Double min, Double max);
    List<Product> findByCaratsBetween(Integer min, Integer max);
    List<Product> findByPriceBetween(Double min, Double max);
}