// package com.goldloan.safegold1.service;

// import com.goldloan.safegold1.model.Loan;
// import com.goldloan.safegold1.model.User;
// import com.goldloan.safegold1.repository.LoanRepository;
// import org.springframework.stereotype.Service;

// import java.util.List;

// @Service
// public class LoanService {

//     private final LoanRepository loanRepository;

//     public LoanService(LoanRepository loanRepository) {
//         this.loanRepository = loanRepository;
//     }

//     public List<Loan> getUserLoans(User user) {
//         return loanRepository.findByUserId(user.getId());
//     }
// }
