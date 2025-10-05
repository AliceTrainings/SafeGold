package com.goldloan.demo.controller;

import com.goldloan.demo.dto.ProductFilterDto;
import com.goldloan.demo.dto.ProductImageDto;
import com.goldloan.demo.dto.ProductResponseDto;
import com.goldloan.demo.entity.Product;
import com.goldloan.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {
    private final ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<ProductResponseDto>> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String karat,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        ProductFilterDto filterDto = ProductFilterDto.builder()
            .categoryId(categoryId)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .karat(karat)
            .searchTerm(search)
            .build();
        
        Page<Product> products = productService.findProductsWithFilters(filterDto, pageable);
        Page<ProductResponseDto> response = products.map(this::convertToDto);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(convertToDto(product));
    }
    
    private ProductResponseDto convertToDto(Product product) {
        return ProductResponseDto.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .price(product.getPrice())
            .weight(product.getWeight())
            .karat(product.getKarat())
            .categoryName(product.getCategory().getName())
            .productCode(product.getProductCode())
            .stockQuantity(product.getStockQuantity())
            .makingCharges(product.getMakingCharges())
            .stoneCharges(product.getStoneCharges())
            .isAvailable(product.getIsAvailable())
            .images(product.getImages().stream()
                .map(img -> ProductImageDto.builder()
                    .id(img.getId())
                    .imageUrl(img.getImageUrl())
                    .altText(img.getAltText())
                    .isPrimary(img.getIsPrimary())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
}
