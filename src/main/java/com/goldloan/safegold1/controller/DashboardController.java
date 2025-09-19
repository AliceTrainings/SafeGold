package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.model.Loan;
import com.goldloan.safegold1.model.Payment;
import com.goldloan.safegold1.repository.LoanRepository;
import com.goldloan.safegold1.repository.PaymentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final LoanRepository loanRepository;
    private final PaymentRepository paymentRepository;

    public DashboardController(LoanRepository loanRepository, PaymentRepository paymentRepository) {
        this.loanRepository = loanRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/users/"; // Not logged in

        model.addAttribute("user", user);

        List<Loan> loans = loanRepository.findByUserId(user.getId());
        model.addAttribute("userLoans", loans);

        // Null-safe pending payments
        List<Payment> reminders = loans.stream()
                .flatMap(loan -> loan.getPayments() != null ? loan.getPayments().stream() : Stream.empty())
                .filter(p -> "Pending".equals(p.getStatus()))
                .collect(Collectors.toList());
        model.addAttribute("reminders", reminders);

        return "dashboard";
    }

    @GetMapping("/loan/pay/{loanId}")
    public String payInterest(@PathVariable Long loanId) {
        List<Payment> payments = paymentRepository.findByLoanId(loanId);
        payments.stream()
                .filter(p -> "Pending".equals(p.getStatus()))
                .findFirst()
                .ifPresent(p -> {
                    p.setStatus("Paid");
                    paymentRepository.save(p);
                });
        return "redirect:/dashboard";
    }

    @GetMapping("/loan/receipt/{paymentId}")
    public String viewReceipt(@PathVariable Long paymentId, Model model) {
        Payment payment = paymentRepository.findById(paymentId).orElse(null);
        if (payment != null) {
            model.addAttribute("payment", payment);
            model.addAttribute("loan", payment.getLoan());
            return "receipt";
        }
        return "redirect:/dashboard";
    }
}
