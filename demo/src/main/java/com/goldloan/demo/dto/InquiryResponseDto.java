package com.goldloan.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import com.goldloan.demo.entity.InquiryStatus; // Make sure this exists in your project

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponseDto {
    private Long id;
    private String userName;
    private String productName;
    private String message;
    private String contactNumber;
    private InquiryStatus status;
    private String adminResponse;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private String respondedBy;
}
