package com.goldloan.demo.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import com.goldloan.demo.entity.Karat;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Weight must be greater than 0")
    private BigDecimal weight;

    @NotNull(message = "Karat is required")
    private Karat karat;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotBlank(message = "Product code is required")
    private String productCode;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private BigDecimal makingCharges;
    private BigDecimal stoneCharges;
}
