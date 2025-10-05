package com.goldloan.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "loan_payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;
    
    @Column(name = "payment_amount", nullable = false)
    private BigDecimal paymentAmount;
    
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;
    
    @Column(name = "payment_method")
    private String paymentMethod; // CASH, CARD, UPI, etc.
    
    @Column(name = "receipt_number")
    private String receiptNumber;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "recorded_by")
    private String recordedBy; // Admin username who recorded the payment
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
