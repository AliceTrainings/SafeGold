package com.safegold.goldloan.controller;

import com.safegold.goldloan.model.User;
import com.safegold.goldloan.repository.UserRepository;
import com.safegold.goldloan.service.OtpService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final OtpService otpService;

    public UserController(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

   @GetMapping("/")
    public String home() {
        return "index"; // serves index.html
   }


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
    public String registerUser(@ModelAttribute User user, Model model) {
         userRepository.save(user);
        model.addAttribute("user", user); // Pass user info to home page
             return "home"; // Redirect to home page after registration
}

}
