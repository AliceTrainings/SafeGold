package com.goldloan.demo.repository;

import com.goldloan.demo.entity.LoanPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDate;

@Repository
public interface LoanPaymentRepository extends JpaRepository<LoanPayment, Long> {

    List<LoanPayment> findByLoanIdOrderByPaymentDateDesc(Long loanId);
    
    @Query("SELECT lp FROM LoanPayment lp WHERE " +
           "lp.paymentDate BETWEEN :startDate AND :endDate " +
           "ORDER BY lp.paymentDate DESC")
    List<LoanPayment> findPaymentsBetweenDates(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
