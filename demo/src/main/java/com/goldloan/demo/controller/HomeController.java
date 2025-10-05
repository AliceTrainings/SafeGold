package com.goldloan.demo.controller;

import com.goldloan.demo.dto.ProductFilterDto;
import com.goldloan.demo.entity.*;
import com.goldloan.demo.service.CategoryService;
import com.goldloan.demo.service.ProductService;
import com.goldloan.demo.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final ProductService productService;
    private final CategoryService categoryService;
    private final WatchlistService watchlistService;
    
    @GetMapping("/")
    public String home(Model model, Authentication auth) {
        // Get featured products (first 8 available products)
        Pageable pageable = PageRequest.of(0, 8);
        ProductFilterDto filterDto = new ProductFilterDto();
        Page<Product> featuredProducts = productService.findProductsWithFilters(filterDto, pageable);
        
        // Get all categories
        List<Category> categories = categoryService.findAllActiveCategories();
        
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        model.addAttribute("categories", categories);
        
        return "index";
    }
    
    @GetMapping("/products")
    public String products(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String karat,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model, Authentication auth) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        ProductFilterDto filterDto = ProductFilterDto.builder()
            .categoryId(categoryId)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .karat(karat)
            .searchTerm(search)
            .build();
        
        Page<Product> products = productService.findProductsWithFilters(filterDto, pageable);
        List<Category> categories = categoryService.findAllActiveCategories();
        
        // Check watchlist status for authenticated users
        if (auth != null) {
            User user = (User) auth.getPrincipal();
            products.getContent().forEach(product -> {
                boolean inWatchlist = watchlistService.isInWatchlist(user.getId(), product.getId());
                model.addAttribute("inWatchlist_" + product.getId(), inWatchlist);
            });
        }
        
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("currentFilters", filterDto);
        model.addAttribute("karatValues", Karat.values());
        
        return "products";
    }
    
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model, Authentication auth) {
        Product product = productService.findById(id);
        
        boolean inWatchlist = false;
        if (auth != null) {
            User user = (User) auth.getPrincipal();
            inWatchlist = watchlistService.isInWatchlist(user.getId(), id);
        }
        
        model.addAttribute("product", product);
        model.addAttribute("inWatchlist", inWatchlist);
        
        return "product-detail";
    }
}
