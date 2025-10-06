package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.UserRepository;
import com.goldloan.safegold1.repository.ProductRepository;
import com.goldloan.safegold1.repository.InquiryRepository;
import com.goldloan.safegold1.model.Product;
import com.goldloan.safegold1.model.Inquiry;
import com.goldloan.safegold1.service.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;

    public UserController(UserRepository userRepository, OtpService otpService, ProductRepository productRepository, InquiryRepository inquiryRepository) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.productRepository = productRepository;
        this.inquiryRepository = inquiryRepository;
    }

    // Home page
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "index";
    }

    @GetMapping("/products")
    public String browseProducts(@RequestParam(required = false) String q,
                                 @RequestParam(required = false) String category,
                                 @RequestParam(required = false) Double minPrice,
                                 @RequestParam(required = false) Double maxPrice,
                                 @RequestParam(required = false) Double minGrams,
                                 @RequestParam(required = false) Double maxGrams,
                                 @RequestParam(required = false) Integer minCarats,
                                 @RequestParam(required = false) Integer maxCarats,
                                 Model model) {
        java.util.List<Product> products = productRepository.findAll();

        if (q != null && !q.isBlank()) {
            products = productRepository.findByNameContainingIgnoreCase(q.trim());
        }
        if (category != null && !category.isBlank()) {
            products = productRepository.findByCategoryIgnoreCase(category.trim());
        }
        if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        }
        if (minGrams != null && maxGrams != null) {
            products = productRepository.findByGramsBetween(minGrams, maxGrams);
        }
        if (minCarats != null && maxCarats != null) {
            products = productRepository.findByCaratsBetween(minCarats, maxCarats);
        }

        model.addAttribute("products", products);
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minGrams", minGrams);
        model.addAttribute("maxGrams", maxGrams);
        model.addAttribute("minCarats", minCarats);
        model.addAttribute("maxCarats", maxCarats);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) {
            return "redirect:/users/products";
        }
        model.addAttribute("product", p);
        model.addAttribute("inquiry", new Inquiry());
        return "product-detail";
    }

    @PostMapping("/products/{id}/inquire")
    public String submitInquiry(@PathVariable Long id,
                                @ModelAttribute Inquiry inquiry,
                                Model model) {
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) {
            return "redirect:/users/products";
        }
        inquiry.setProduct(p);
        inquiryRepository.save(inquiry);
        return "redirect:/users/products/" + id + "?inquiry=success";
    }

    // Phone number page
    @GetMapping("/phone")
    public String showPhoneForm() {
        return "phone";
    }

    // Send OTP (original + resend)
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String phone, Model model) {
        phone = phone.trim();
        String otp = otpService.generateOtp();
        otpService.saveOtp(phone, otp);

        model.addAttribute("phone", phone);
        model.addAttribute("otp", otp); // debug/testing only
        return "otp";
    }

    // Verify OTP
    @PostMapping("/otp")
    public String verifyOtp(@RequestParam String phone,
                            @RequestParam String otp,
                            Model model) {
        phone = phone.trim();
        if (otpService.validateOtp(phone, otp)) {
            Optional<User> existingUser = userRepository.findByMobileNumber(phone);
            if (existingUser.isPresent()) {
                // Existing user → login page
                model.addAttribute("mobileNumber", phone);
                return "login";
            } else {
                // New user → register page
                model.addAttribute("phone", phone);
                return "register";
            }
        }

        model.addAttribute("phone", phone);
        model.addAttribute("error", "Invalid OTP. Please try again.");
        return "otp";
    }

    // Registration page (GET)
    @GetMapping("/register")
    public String showRegisterPage(@RequestParam(required = false) String phone, Model model) {
        if (phone != null) {
            model.addAttribute("phone", phone);
        }
        return "register";
    }

    // Register new user
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, HttpSession session, Model model) {
        String phone = user.getMobileNumber().trim();
        Optional<User> existingUser = userRepository.findByMobileNumber(phone);

        if (existingUser.isPresent()) {
            model.addAttribute("error", "Mobile number already registered. Please login.");
            model.addAttribute("mobileNumber", phone);
            return "login";
        }

        user.setMobileNumber(phone);
        userRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/users/";
    }

    // Login page (GET)
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String mobileNumber, Model model) {
        if (mobileNumber != null) {
            model.addAttribute("mobileNumber", mobileNumber);
        }
        return "login";
    }

    // Login submission (POST)
    @PostMapping("/login")
    public String loginUser(@RequestParam String mobileNumber,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        String phone = mobileNumber.trim();
        Optional<User> userOpt = userRepository.findByMobileNumber(phone);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword().equals(password)) {
                session.setAttribute("user", user);
                return "redirect:/users/"; // home page
            } else {
                model.addAttribute("error", "Invalid password");
            }
        } else {
            model.addAttribute("error", "User not found");
        }

        model.addAttribute("mobileNumber", phone);
        return "login";
    }

    @PostMapping("/watchlist/{productId}/add")
    public String addToWatchlist(@PathVariable Long productId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        Product p = productRepository.findById(productId).orElse(null);
        if (p != null) {
            user.getWatchlist().add(p);
            userRepository.save(user);
        }
        return "redirect:/users/products/" + productId;
    }

    @PostMapping("/watchlist/{productId}/remove")
    public String removeFromWatchlist(@PathVariable Long productId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        Product p = productRepository.findById(productId).orElse(null);
        if (p != null) {
            user.getWatchlist().remove(p);
            userRepository.save(user);
        }
        return "redirect:/users/products/" + productId;
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/";
    }
}
