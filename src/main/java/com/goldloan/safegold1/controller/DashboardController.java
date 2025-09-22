package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.Loan;
import com.goldloan.safegold1.model.Payment;
import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.LoanRepository;
import jakarta.servlet.http.HttpServletResponse;
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

        // Fetch all loans
        List<Loan> allLoans = loanRepository.findByUserId(user.getId());
        user.setLoans(allLoans);

        // Active & Past loans
        List<Loan> activeLoans = allLoans.stream()
                .filter(loan -> "Active".equalsIgnoreCase(loan.getStatus()))
                .collect(Collectors.toList());

        List<Loan> pastLoans = allLoans.stream()
                .filter(loan -> "Closed".equalsIgnoreCase(loan.getStatus()))
                .collect(Collectors.toList());

        // Payment history
        List<Payment> paymentHistory = allLoans.stream()
                .flatMap(loan -> loan.getPayments().stream())
                .collect(Collectors.toList());

        model.addAttribute("user", user);
        model.addAttribute("activeLoans", activeLoans);
        model.addAttribute("pastLoans", pastLoans);
        model.addAttribute("paymentHistory", paymentHistory);

        return "dashboard";
    }

    // ✅ PDF Export for Payment History
    @GetMapping("/download/payments/pdf")
    public void downloadPaymentsPdf(HttpSession session, HttpServletResponse response) throws Exception {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("/users/login");
            return;
        }

        List<Loan> allLoans = loanRepository.findByUserId(user.getId());
        List<Payment> paymentHistory = allLoans.stream()
                .flatMap(loan -> loan.getPayments().stream())
                .collect(Collectors.toList());

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=payment_history.pdf");

        com.lowagie.text.Document document = new com.lowagie.text.Document();
        com.lowagie.text.pdf.PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Title
        com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Payment History Report");
        title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
        document.add(title);
        document.add(new com.lowagie.text.Paragraph(" "));

        // Table
        com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(6);
        table.setWidthPercentage(100);
        table.addCell("Payment ID");
        table.addCell("Loan ID");
        table.addCell("Amount");
        table.addCell("Remaining");
        table.addCell("Status");
        table.addCell("Date");

        for (Payment payment : paymentHistory) {
            table.addCell(String.valueOf(payment.getId()));
            table.addCell(String.valueOf(payment.getLoan().getId()));
            table.addCell(String.valueOf(payment.getAmount()));
            table.addCell(String.valueOf(payment.getRemaining()));
            table.addCell(payment.getStatus());
            table.addCell(payment.getDate().toString());
        }

        document.add(table);
        document.close();
    }
}
