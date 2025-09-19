package com.goldloan.safegold1.repository;

import com.goldloan.safegold1.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId); // fetch all loans for a specific user
}
 
