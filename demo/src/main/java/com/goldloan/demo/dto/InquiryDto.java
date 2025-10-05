package com.goldloan.demo.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryDto {

    private Long productId;
    private String message;
    private String contactNumber;
}
