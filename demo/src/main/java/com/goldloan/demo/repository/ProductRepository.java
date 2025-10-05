package com.goldloan.demo.repository;

import com.goldloan.demo.entity.Product;
import com.goldloan.demo.entity.Karat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // All available products
    List<Product> findByIsAvailableTrue();

    // Filter by category
    List<Product> findByCategoryId(Long categoryId);

    // Filter by Karat (enum)
    List<Product> findByKarat(Karat karat);

    // Custom search with filters + pagination
    @Query("SELECT p FROM Product p WHERE p.isAvailable = true " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:karat IS NULL OR p.karat = :karat) " +
           "AND (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> findProductsWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("karat") Karat karat,
            @Param("searchTerm") String searchTerm,
            Pageable pageable
    );
}
