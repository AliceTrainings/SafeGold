package com.goldloan.safegold1.repository;

import com.goldloan.safegold1.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    // Fetch loans by user ID
    List<Loan> findByUserId(Long userId);

    // Fetch loans by status
    List<Loan> findByStatus(String status);

    // Optional: fetch loans with payments eagerly to avoid lazy loading issues
    @Query("SELECT l FROM Loan l LEFT JOIN FETCH l.payments WHERE l.user.id = :userId")
    List<Loan> findByUserIdWithPayments(@Param("userId") Long userId);
}