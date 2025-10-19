package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.Loan;
import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.LoanRepository;
import com.goldloan.safegold1.service.ReportService;
import com.goldloan.safegold1.service.WatchlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import com.goldloan.safegold1.repository.InquiryRepository;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final LoanRepository loanRepository;
    private final ReportService reportService;
    private final WatchlistService watchlistService;

    public DashboardController(LoanRepository loanRepository, ReportService reportService, InquiryRepository inquiryRepository, WatchlistService watchlistService) {
        this.loanRepository = loanRepository;
        this.reportService = reportService;
        this.watchlistService = watchlistService;
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

        // Get watchlist count
        int watchlistCount = watchlistService.getWatchlistCount(user.getId());
        System.out.println("Dashboard for user " + user.getId() + " - watchlist count: " + watchlistCount);

        // Add attributes to model
        model.addAttribute("user", user);
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("pastLoans", pastLoans);
        model.addAttribute("paymentHistory", paymentHistory);
        model.addAttribute("watchlistCount", watchlistCount);

        return "dashboard"; // Thymeleaf template
    }

    @GetMapping("/download/pdf")
    public ResponseEntity<byte[]> downloadAllAsPdf(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(302).header(HttpHeaders.LOCATION, "/users/login").build();
        }
        List<Loan> loans = loanRepository.findByUserIdWithPayments(user.getId());
        byte[] pdf = reportService.buildLoansAndPaymentsPdf(loans, "Loans & Payments Report");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=loans-payments.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}