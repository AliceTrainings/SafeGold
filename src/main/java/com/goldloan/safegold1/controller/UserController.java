package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.UserRepository;
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

    public UserController(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    // Home page
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
        }
        return "index"; // index.html
    }

    // Phone number input
    @GetMapping("/phone")
    public String showPhoneForm() {
        return "phone"; // phone.html
    }

    // Send OTP
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String phone, Model model) {
        phone = phone.trim();
        String otp = otpService.generateOtp();
        otpService.saveOtp(phone, otp);
        model.addAttribute("phone", phone);
        model.addAttribute("otp", otp); // for debug/testing
        return "otp"; // otp.html
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
                model.addAttribute("mobileNumber", phone);
                return "login"; // existing user → login
            } else {
                model.addAttribute("phone", phone);
                return "register"; // new user → register
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
        return "register"; // register.html
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
        return "redirect:/users/"; // redirect to home
    }

    // Login page (GET)
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String mobileNumber, Model model) {
        if (mobileNumber != null) {
            model.addAttribute("mobileNumber", mobileNumber);
        }
        return "login"; // login.html
    }

    // Handle login submission (POST)
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

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/";
    }
}
