package com.goldloan.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String imageUrl;

    @Builder.Default
    private Integer sortOrder = 0;
}
