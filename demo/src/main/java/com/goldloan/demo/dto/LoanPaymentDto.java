package com.goldloan.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.DecimalMin;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanPaymentDto {
    @NotNull(message = "Loan ID is required")
    private Long loanId;
    
    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "1.0", message = "Payment amount must be positive")
    private BigDecimal paymentAmount;
    
    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String receiptNumber;
    private String notes;
    
    @NotBlank(message = "Recorded by is required")
    private String recordedBy;
}