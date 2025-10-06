package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.Loan;
import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.LoanRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final LoanRepository loanRepository;

    public DashboardController(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    @GetMapping({"", "/"})
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }

        // Fetch all loans of the user
        List<Loan> allLoans = loanRepository.findByUserId(user.getId());
        user.setLoans(allLoans);

        // Separate active and past loans
        List<Loan> activeLoans = allLoans.stream()
                .filter(loan -> "Active".equalsIgnoreCase(loan.getStatus()))
                .collect(Collectors.toList());

        List<Loan> pastLoans = allLoans.stream()
                .filter(loan -> "Closed".equalsIgnoreCase(loan.getStatus()))
                .collect(Collectors.toList());

        // Payment history
        List<com.goldloan.safegold1.model.Payment> paymentHistory = allLoans.stream()
                .flatMap(loan -> loan.getPayments().stream())
                .collect(Collectors.toList());

        // Add attributes to model
        model.addAttribute("user", user);
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("pastLoans", pastLoans);
        model.addAttribute("paymentHistory", paymentHistory);

        return "dashboard"; // Thymeleaf template
    }
}
