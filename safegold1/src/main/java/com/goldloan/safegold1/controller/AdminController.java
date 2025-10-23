package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.UserRepository;
import com.goldloan.safegold1.model.Product;
import com.goldloan.safegold1.repository.ProductRepository;
import com.goldloan.safegold1.repository.InquiryRepository;
import com.goldloan.safegold1.model.Inquiry;
import com.goldloan.safegold1.repository.LoanRepository;
import com.goldloan.safegold1.model.Loan;
import com.goldloan.safegold1.service.FileUploadService;
import com.goldloan.safegold1.service.ProductPriceService;
import jakarta.servlet.http.HttpSession;
import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;
    private final LoanRepository loanRepository;
    private final FileUploadService fileUploadService;
    private final ProductPriceService productPriceService;

    public AdminController(UserRepository userRepository, ProductRepository productRepository, InquiryRepository inquiryRepository, LoanRepository loanRepository, FileUploadService fileUploadService, ProductPriceService productPriceService) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.inquiryRepository = inquiryRepository;
        this.loanRepository = loanRepository;
        this.fileUploadService = fileUploadService;
        this.productPriceService = productPriceService;
    }

    @GetMapping("/login")
    public String showAdminLogin(@RequestParam(required = false) String email, Model model) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "admin-login";
    }

    @PostMapping("/login")
    public String handleAdminLogin(@RequestParam String email,
                                   @RequestParam String password,
                                   HttpSession session,
                                   Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email.trim());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("ADMIN".equalsIgnoreCase(user.getRole()) && user.getPassword().equals(password)) {
                session.setAttribute("admin", user);
                return "redirect:/admin/dashboard";
            } else {
                model.addAttribute("error", "Invalid credentials or not an admin");
            }
        } else {
            model.addAttribute("error", "Admin not found");
        }
        model.addAttribute("email", email);
        return "admin-login";
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("admin", admin);
        // Add high-level stats for the template
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalProducts", productRepository.count());
        model.addAttribute("totalInquiries", inquiryRepository.count());
        
        // Count active and closed loans
        long activeLoansCount = loanRepository.findByStatus("Active").size();
        long closedLoansCount = loanRepository.findByStatus("Closed").size();
        
        model.addAttribute("activeLoans", activeLoansCount);
        model.addAttribute("closedLoans", closedLoansCount);
        return "admin-dashboard";
    }

    // ADMIN PRODUCTS
    @GetMapping("/products")
    public String listProducts(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("admin", admin);
        model.addAttribute("products", productRepository.findAll());
        return "admin-products";
    }

    @GetMapping("/products/new")
    public String newProductForm(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("admin", admin);
        model.addAttribute("product", new Product());
        return "admin-product-form";
    }

    @PostMapping("/products")
    public String createProduct(@RequestParam String name,
                                @RequestParam String category,
                                @RequestParam Double grams,
                                @RequestParam Integer carats,
                                @RequestParam MultipartFile imageFile,
                                @RequestParam(required = false) String gallery,
                                @RequestParam Double price,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String tags,
                                HttpSession session,
                                Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            // Upload image file
            String imageUrl = fileUploadService.uploadImage(imageFile);
            if (imageUrl == null) {
                model.addAttribute("error", "Failed to upload image. Please try again.");
                return "admin-product-form";
            }
            
            Product p = new Product();
            p.setName(name.trim());
            p.setCategory(category.trim());
            p.setGrams(grams);
            p.setCarats(carats);
            p.setImageUrl(imageUrl);
            p.setGallery(gallery);
            p.setPrice(price);
            p.setDescription(description);
            p.setTags(tags);
            productRepository.save(p);
            return "redirect:/admin/products?success=Product created successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Error creating product: " + e.getMessage());
            return "admin-product-form";
        }
    }

@GetMapping("/inquiries")
public String viewInquiries(HttpSession session, Model model) {
    User admin = (User) session.getAttribute("admin");
    if (admin == null) {
        return "redirect:/admin/login";
    }

    List<Inquiry> inquiries = inquiryRepository.findAll();
    System.out.println("Fetched inquiries: " + inquiries.size());
    for (Inquiry i : inquiries) {
        System.out.println("Inquiry: " + i.getName() + " | Product: " + 
            (i.getProduct() != null ? i.getProduct().getName() : "null"));
    }

    model.addAttribute("inquiries", inquiries);
    return "admin-inquiries";
}



    // ADMIN LOANS
    @GetMapping("/loans")
    public String listLoans(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("loans", loanRepository.findAll());
        return "admin-loans";
    }

    @GetMapping("/loans/new")
    public String newLoanForm(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("today", java.time.LocalDate.now());
        return "admin-loan-form";
    }

    @PostMapping("/loans")
    public String createLoan(@RequestParam(required = false) String userEmail,
                             @RequestParam(required = false) String userMobile,
                             @RequestParam Double amount,
                             @RequestParam String goldDescription,
                             @RequestParam Double interestRate,
                             @RequestParam Integer tenure,
                             @RequestParam(required = false) String status,
                             @RequestParam(required = false) java.time.LocalDate startDate,
                             HttpSession session,
                             Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }

        java.util.Optional<User> userOpt = java.util.Optional.empty();
        if (userEmail != null && !userEmail.isBlank()) {
            userOpt = userRepository.findByEmail(userEmail.trim());
        } else if (userMobile != null && !userMobile.isBlank()) {
            userOpt = userRepository.findByMobileNumber(userMobile.trim());
        }
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "User not found by email or mobile");
            model.addAttribute("today", java.time.LocalDate.now());
            return "admin-loan-form";
        }

        Loan loan = new Loan();
        loan.setUser(userOpt.get());
        loan.setAmount(amount);
        loan.setGoldDescription(goldDescription);
        loan.setInterestRate(interestRate);
        loan.setTenure(tenure);
        loan.setStatus((status == null || status.isBlank()) ? "Active" : status.trim());
        loan.setStartDate(startDate == null ? java.time.LocalDate.now() : startDate);
        loanRepository.save(loan);
        return "redirect:/admin/loans";
    }

    @PostMapping("/loans/{id}/status")
    public String updateLoanStatus(@PathVariable Long id,
                                   @RequestParam String status,
                                   HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        Loan loan = loanRepository.findById(id).orElse(null);
        if (loan != null) {
            loan.setStatus(status);
            loanRepository.save(loan);
        }
        return "redirect:/admin/loans";
    }

    @GetMapping("/loans/active")
    public ResponseEntity<java.util.List<Loan>> getActiveLoans(HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return ResponseEntity.status(401).build();
        }
        java.util.List<Loan> activeLoans = loanRepository.findByStatus("Active");
        return ResponseEntity.ok(activeLoans);
    }

    @GetMapping("/products/{id}/edit")
    public String editProductForm(@PathVariable Long id, HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) {
            return "redirect:/admin/products";
        }
        model.addAttribute("product", p);
        return "admin-product-form";
    }

    @PostMapping("/products/{id}")
    public String updateProduct(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String category,
                                @RequestParam Double grams,
                                @RequestParam Integer carats,
                                @RequestParam(required = false) MultipartFile imageFile,
                                @RequestParam(required = false) String gallery,
                                @RequestParam Double price,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) String tags,
                                HttpSession session,
                                Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) {
            return "redirect:/admin/products";
        }
        
        try {
            // Update image only if new file is provided
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old image
                fileUploadService.deleteImage(p.getImageUrl());
                
                // Upload new image
                String imageUrl = fileUploadService.uploadImage(imageFile);
                if (imageUrl != null) {
                    p.setImageUrl(imageUrl);
                }
            }
            
            p.setName(name.trim());
            p.setCategory(category.trim());
            p.setGrams(grams);
            p.setCarats(carats);
            p.setGallery(gallery);
            p.setPrice(price);
            p.setDescription(description);
            p.setTags(tags);
            productRepository.save(p);
            return "redirect:/admin/products?success=Product updated successfully";
        } catch (Exception e) {
            model.addAttribute("error", "Error updating product: " + e.getMessage());
            model.addAttribute("product", p);
            return "admin-product-form";
        }
    }

    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable Long id, HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        // Get product to delete associated image
        Product product = productRepository.findById(id).orElse(null);
        if (product != null) {
            // Delete associated image file
            fileUploadService.deleteImage(product.getImageUrl());
            productRepository.deleteById(id);
        }
        
        return "redirect:/admin/products?success=Product deleted successfully";
    }

    @PostMapping("/products/update-prices")
    public String updateAllProductPrices(HttpSession session) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        try {
            productPriceService.updateAllProductPrices();
            return "redirect:/admin/products?success=All product prices updated successfully based on current gold rates";
        } catch (Exception e) {
            return "redirect:/admin/products?error=Failed to update prices: " + e.getMessage();
        }
    }

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("admin");
        return "redirect:/admin/login";
    }


    // User list page
    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        List<User> users = userRepository.findAll();
        model.addAttribute("admin", admin);
        model.addAttribute("users", users);
        return "admin-users";
    }

    // User details page
    @GetMapping("/users/{id}")
    public String viewUserDetails(@PathVariable Long id, HttpSession session, Model model) {
        User admin = (User) session.getAttribute("admin");
        if (admin == null) {
            return "redirect:/admin/login";
        }
        
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Loan> userLoans = loanRepository.findByUserId(id);
            
            model.addAttribute("admin", admin);
            model.addAttribute("user", user);
            model.addAttribute("userLoans", userLoans);
            return "admin-user-details";
        } else {
            return "redirect:/admin/users";
        }
    }
}