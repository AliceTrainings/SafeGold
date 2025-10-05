package com.goldloan.demo.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loanNumber;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private BigDecimal goldWeight;
    private BigDecimal goldRatePerGram;
    private BigDecimal loanAmount;
    private BigDecimal interestRate;
    private BigDecimal totalPayable;
    private BigDecimal amountPaid;
    private BigDecimal balanceAmount;

    @Enumerated(EnumType.STRING)
    private Karat karat;

    private int durationMonths;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private String notes;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    // ✅ Add createdAt for repository sorting
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
