// package com.goldloan.safegold1.service;

// import com.goldloan.safegold1.model.Payment;
// import com.goldloan.safegold1.repository.PaymentRepository;
// import org.springframework.stereotype.Service;

// import java.util.List;

// @Service
// public class PaymentService {

//     private final PaymentRepository paymentRepository;

//     public PaymentService(PaymentRepository paymentRepository) {
//         this.paymentRepository = paymentRepository;
//     }

//     // Fetch all payments for a loan
//     public List<Payment> getPaymentsByLoan(Long loanId) {
//         return paymentRepository.findByLoanId(loanId);
//     }

//     // Save or update a payment
//     public Payment savePayment(Payment payment) {
//         return paymentRepository.save(payment);
//     }
// }
