package com.goldloan.demo.controller;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.security.access.prepost.PreAuthorize;

import lombok.RequiredArgsConstructor;

import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import com.goldloan.demo.entity.Product;
import com.goldloan.demo.entity.Inquiry;
import com.goldloan.demo.entity.Loan;
import com.goldloan.demo.entity.User;
import com.goldloan.demo.repository.LoanRepository;
import com.goldloan.demo.service.ProductService;
import com.goldloan.demo.service.InquiryService;
import com.goldloan.demo.service.LoanService;
import com.goldloan.demo.dto.ProductFilterDto;
import com.goldloan.demo.service.UserService;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;
    private final InquiryService inquiryService;
    private final LoanService loanService;
    private final UserService userService;
    private final LoanRepository loanRepository;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Get dashboard metrics
        long totalActiveLoans = loanRepository.countActiveLoans();
        BigDecimal totalLoanAmount = loanRepository.getTotalActiveLoansAmount();
        
        List<Inquiry> recentInquiries = inquiryService.getAllInquiries(PageRequest.of(0, 5)).getContent();
        List<Loan> recentLoans = loanService.getAllLoans(PageRequest.of(0, 5, Sort.by("createdAt").descending())).getContent();
        
        model.addAttribute("totalActiveLoans", totalActiveLoans);
        model.addAttribute("totalLoanAmount", totalLoanAmount != null ? totalLoanAmount : BigDecimal.ZERO);
        model.addAttribute("recentInquiries", recentInquiries);
        model.addAttribute("recentLoans", recentLoans);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/products")
    public String products(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        ProductFilterDto filterDto = new ProductFilterDto();
        Page<Product> products = productService.findProductsWithFilters(filterDto, pageable);
        
        model.addAttribute("products", products);
        return "admin/products";
    }
    
    @GetMapping("/inquiries")
    public String inquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Inquiry> inquiries = inquiryService.getAllInquiries(pageable);
        
        model.addAttribute("inquiries", inquiries);
        return "admin/inquiries";
    }
    
    @GetMapping("/loans")
    public String loans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Loan> loans = loanService.getAllLoans(pageable);
        
        model.addAttribute("loans", loans);
        return "admin/loans";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }
}