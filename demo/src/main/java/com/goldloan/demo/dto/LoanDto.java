package com.goldloan.demo.dto;

import com.goldloan.demo.entity.Karat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class LoanDto {
    private Long userId;
    private BigDecimal goldWeight;
    private BigDecimal goldRatePerGram;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private Karat karat;
    private int loanDurationMonths;
    private LocalDate startDate;
    private String notes;
}
