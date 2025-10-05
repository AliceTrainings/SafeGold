package com.goldloan.demo.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterDto {
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String karat;
    private String searchTerm;
}
