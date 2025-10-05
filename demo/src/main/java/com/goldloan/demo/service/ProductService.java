package com.goldloan.demo.service;

import com.goldloan.demo.dto.ProductFilterDto;
import com.goldloan.demo.entity.Karat;
import com.goldloan.demo.entity.Product;
import com.goldloan.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> findProductsWithFilters(ProductFilterDto filterDto, Pageable pageable) {
        // Convert karat string to enum if provided
        Karat karatEnum = null;
        if (filterDto.getKarat() != null && !filterDto.getKarat().isEmpty()) {
            try {
                karatEnum = Karat.valueOf(filterDto.getKarat().toUpperCase());
            } catch (IllegalArgumentException e) {
                karatEnum = null; // invalid value, ignore
            }
        }

        // Call the correct repository method
        return productRepository.findProductsWithFilters(
                filterDto.getCategoryId(),
                filterDto.getMinPrice(),
                filterDto.getMaxPrice(),
                karatEnum,
                filterDto.getSearchTerm(),
                pageable
        );
    }

    // Fetch a single product by ID
    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }
}
