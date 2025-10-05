package com.goldloan.demo.dto;


import java.util.List;              // for List
import com.goldloan.demo.entity.Karat; // for Karat enum
import lombok.AllArgsConstructor;   // for @AllArgsConstructor
import lombok.NoArgsConstructor;    // for @NoArgsConstructor if used
import lombok.Data;                 // for @Data if used
import lombok.Builder; 
import java.math.BigDecimal;       
// for BigDecimal             
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal weight;
    private Karat karat;
    private String categoryName;
    private String productCode;
    private Integer stockQuantity;
    private BigDecimal makingCharges;
    private BigDecimal stoneCharges;
    private Boolean isAvailable;
    private List<ProductImageDto> images;
}
