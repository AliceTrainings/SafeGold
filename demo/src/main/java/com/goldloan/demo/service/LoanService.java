package com.goldloan.demo.service;

import com.goldloan.demo.dto.LoanDto;
import com.goldloan.demo.dto.LoanPaymentDto;
import com.goldloan.demo.entity.Loan;
import com.goldloan.demo.entity.LoanStatus;
import com.goldloan.demo.entity.User;
import com.goldloan.demo.repository.LoanRepository;
import com.goldloan.demo.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public Loan createLoan(LoanDto loanDto) {
        User user = userRepository.findById(loanDto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        BigDecimal totalLoanAmount = loanDto.getLoanAmount()
                .add(loanDto.getLoanAmount()
                .multiply(loanDto.getInterestRate())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

        Loan loan = Loan.builder()
                .user(user)
                .goldWeight(loanDto.getGoldWeight())
                .karat(loanDto.getKarat())
                .goldRatePerGram(loanDto.getGoldRatePerGram())
                .loanAmount(loanDto.getLoanAmount())
                .interestRate(loanDto.getInterestRate())
                .totalPayable(totalLoanAmount)
                .startDate(loanDto.getStartDate())
                .durationMonths(loanDto.getLoanDurationMonths())
                .notes(loanDto.getNotes())
                .status(LoanStatus.ACTIVE)
                .amountPaid(BigDecimal.ZERO)
                .balanceAmount(totalLoanAmount)
                .build();

        return loanRepository.save(loan);
    }

    public Loan makePayment(Long loanId, LoanPaymentDto paymentDto) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new EntityNotFoundException("Loan not found"));

        loan.setAmountPaid(loan.getAmountPaid().add(paymentDto.getPaymentAmount()));
        loan.setBalanceAmount(loan.getTotalPayable().subtract(loan.getAmountPaid()));

        if (loan.getBalanceAmount().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }

        return loanRepository.save(loan);
    }

    public List<Loan> getUserLoans(Long userId) {
        return loanRepository.findByUserIdOrderByCreatedAtDesc(userId)
;
    }

    public Page<Loan> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable);
    }
}
