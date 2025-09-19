package com.goldloan.safegold1.controller;

import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.UserRepository;
import com.goldloan.safegold1.service.OtpService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        return "index"; // home page HTML
    }

    // Phone number page
    @GetMapping("/phone")
    public String showPhoneForm() {
        return "phone";
    }

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String phone, Model model) {
        String otp = otpService.generateOtp();
        otpService.saveOtp(phone, otp);
        model.addAttribute("phone", phone);
        model.addAttribute("otp", otp); // dev only
        return "otp";
    }

    @PostMapping("/otp")
    public String verifyOtp(@RequestParam String phone,
                            @RequestParam String otp,
                            Model model) {
        if (otpService.validateOtp(phone, otp)) {
            model.addAttribute("phone", phone);
            return "register";
        }
        model.addAttribute("phone", phone);
        model.addAttribute("error", "Invalid OTP. Please try again.");
        return "otp";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, HttpSession session) {
        userRepository.save(user);
        // Store user in session
        session.setAttribute("user", user);
        return "redirect:/users/"; // redirect to home page
    }

    // Dashboard
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            return "dashboard";
        }
        return "redirect:/users/"; // redirect to home if not logged in
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // clear session
        return "redirect:/users/"; // return to home without user
    }
}
