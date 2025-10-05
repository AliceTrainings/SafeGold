package com.goldloan.demo.repository;

import com.goldloan.demo.entity.Loan;
import com.goldloan.demo.entity.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.math.BigDecimal;




@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    List<Loan> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Loan> findByStatusOrderByCreatedAtDesc(LoanStatus status);

    List<Loan> findByUserIdOrderByStartDateDesc(Long userId); // ADD THIS

    Optional<Loan> findByLoanNumber(String loanNumber);




    @Query("SELECT l FROM Loan l WHERE " +
           "(:status IS NULL OR l.status = :status) " +
           "AND (:startDate IS NULL OR l.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR l.createdAt <= :endDate)")
    List<Loan> findLoansForReport(
        @Param("status") LoanStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(l) FROM Loan l WHERE l.status = 'ACTIVE'")
    long countActiveLoans();

    @Query("SELECT SUM(l.loanAmount) FROM Loan l WHERE l.status = 'ACTIVE'")
    BigDecimal getTotalActiveLoansAmount();
}
