package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.UserRepository;
import com.goldloan.safegold1.repository.ProductRepository;
import com.goldloan.safegold1.model.Product;
import com.goldloan.safegold1.model.Inquiry;
import com.goldloan.safegold1.repository.InquiryRepository;
import com.goldloan.safegold1.service.OtpService;
import com.goldloan.safegold1.service.WatchlistService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final ProductRepository productRepository;
    private final InquiryRepository inquiryRepository;
    private final WatchlistService watchlistService;

    public UserController(UserRepository userRepository, OtpService otpService, ProductRepository productRepository, InquiryRepository inquiryRepository, WatchlistService watchlistService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.productRepository = productRepository;
        this.inquiryRepository = inquiryRepository;
        this.watchlistService = watchlistService;
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

        // In your Spring Boot controller
    @RestController
    @RequestMapping("/api/metals")
    public class MetalController {

        @GetMapping("/{metal}")
        public ResponseEntity<String> getMetalPrice(@PathVariable String metal) {
            String apiKey = "goldapi-5z18ld4gkwkcye86-io";
            String url = "https://www.goldapi.io/api/" + metal + "/INR";

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", apiKey);
            headers.set("Content-Type", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            return response;
        }
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
                                 Model model,
                                 HttpSession session) {
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

        // Get user from session
        User user = (User) session.getAttribute("user");
        
        model.addAttribute("products", products);
        model.addAttribute("q", q);
        model.addAttribute("category", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("minGrams", minGrams);
        model.addAttribute("maxGrams", maxGrams);
        model.addAttribute("minCarats", minCarats);
        model.addAttribute("maxCarats", maxCarats);
        model.addAttribute("user", user);
        return "products";
    }

    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model, HttpSession session) {
        Product p = productRepository.findById(id).orElse(null);
        if (p == null) {
            return "redirect:/users/products";
        }
        
        // Get user from session
        User user = (User) session.getAttribute("user");
        
        model.addAttribute("product", p);
        model.addAttribute("inquiry", new Inquiry());
        model.addAttribute("user", user);
        return "product-detail";
    }

    @PostMapping("/products/{id}/inquire")
    public String submitInquiry(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam String phone,
                                @RequestParam(required = false) String email,
                                @RequestParam(required = false) String message,
                                Model model,
                                HttpSession session) {
        try {
            Product p = productRepository.findById(id).orElse(null);
            if (p == null) {
                model.addAttribute("errorMessage", "Product not found. The product you're trying to inquire about may have been removed.");
                model.addAttribute("errorDetails", "Product ID: " + id + " not found in database");
                return "error";
            }
            
            // Validate required fields
            if (name == null || name.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Name is required for inquiry submission.");
                model.addAttribute("errorDetails", "Please provide your name in the inquiry form.");
                return "error";
            }
            
            if (phone == null || phone.trim().isEmpty()) {
                model.addAttribute("errorMessage", "Phone number is required for inquiry submission.");
                model.addAttribute("errorDetails", "Please provide your phone number in the inquiry form.");
                return "error";
            }
            
            // Validate phone number format
            if (!phone.matches("\\d{10}")) {
                model.addAttribute("errorMessage", "Invalid phone number format.");
                model.addAttribute("errorDetails", "Phone number must be exactly 10 digits.");
                return "error";
            }
            
            // Create a new Inquiry object to avoid entity state issues
            Inquiry inquiry = new Inquiry();
            inquiry.setProduct(p);
            inquiry.setName(name.trim());
            inquiry.setPhone(phone.trim());
            inquiry.setEmail(email != null ? email.trim() : null);
            inquiry.setMessage(message != null ? message.trim() : null);
            
            // associate to user if logged in
            Object userObj = session.getAttribute("user");
            if (userObj instanceof com.goldloan.safegold1.model.User u) {
                inquiry.setName(u.getName() != null ? u.getName() : inquiry.getName());
                inquiry.setEmail(u.getEmail() != null ? u.getEmail() : inquiry.getEmail());
                inquiry.setPhone(u.getMobileNumber() != null ? u.getMobileNumber() : inquiry.getPhone());
            }
            
            inquiryRepository.save(inquiry);
            return "redirect:/users/products/" + id + "?inquiry=success";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to submit your inquiry. Please try again later.");
            model.addAttribute("errorDetails", "Database error: " + e.getMessage());
            return "error";
        }
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
    public String addToWatchlist(@PathVariable Long productId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        
        System.out.println("Adding product " + productId + " to watchlist for user " + user.getId());
        
        boolean added = watchlistService.addToWatchlist(user.getId(), productId);
        if (added) {
            model.addAttribute("successMessage", "Product added to watchlist successfully!");
            System.out.println("Successfully added product " + productId + " to watchlist");
        } else {
            model.addAttribute("errorMessage", "Product is already in your watchlist or could not be added.");
            System.out.println("Failed to add product " + productId + " to watchlist");
        }
        
        return "redirect:/users/products/" + productId;
    }

    @PostMapping("/watchlist/{productId}/remove")
    public String removeFromWatchlist(@PathVariable Long productId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        
        System.out.println("Removing product " + productId + " from watchlist for user " + user.getId());
        
        boolean removed = watchlistService.removeFromWatchlist(user.getId(), productId);
        if (removed) {
            model.addAttribute("successMessage", "Product removed from watchlist successfully!");
            System.out.println("Successfully removed product " + productId + " from watchlist");
        } else {
            model.addAttribute("errorMessage", "Product was not in your watchlist or could not be removed.");
            System.out.println("Failed to remove product " + productId + " from watchlist");
        }
        
        return "redirect:/users/products/" + productId;
    }

    @GetMapping("/watchlist")
    public String viewWatchlist(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        
        // Refresh user data to get latest watchlist
        User freshUser = userRepository.findById(user.getId()).orElse(user);
        session.setAttribute("user", freshUser);
        
        System.out.println("Viewing watchlist for user " + freshUser.getId() + " with " + 
                          (freshUser.getWatchlist() != null ? freshUser.getWatchlist().size() : 0) + " items");
        
        model.addAttribute("user", freshUser);
        model.addAttribute("watchlist", freshUser.getWatchlist());
        return "watchlist";
    }

    // Debug endpoint to check watchlist status
    @GetMapping("/debug/watchlist")
    @ResponseBody
    public String debugWatchlist(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "User not logged in";
        }
        
        User freshUser = userRepository.findById(user.getId()).orElse(user);
        int count = freshUser.getWatchlist() != null ? freshUser.getWatchlist().size() : 0;
        
        StringBuilder result = new StringBuilder();
        result.append("User ID: ").append(freshUser.getId()).append("\n");
        result.append("User Name: ").append(freshUser.getName()).append("\n");
        result.append("Watchlist Count: ").append(count).append("\n");
        result.append("Watchlist Items:\n");
        
        if (freshUser.getWatchlist() != null) {
            for (Product product : freshUser.getWatchlist()) {
                result.append("- ").append(product.getName()).append(" (ID: ").append(product.getId()).append(")\n");
            }
        }
        
        return result.toString();
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/";
    }

    // Global error handler for this controller
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        model.addAttribute("errorDetails", "Error: " + e.getMessage());
        return "error";
    }
}