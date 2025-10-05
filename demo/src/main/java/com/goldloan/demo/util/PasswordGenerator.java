package com.goldloan.demo.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        // Create encoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Set your raw password
        String rawPassword = "admin123";

        // Generate BCrypt hash
        String encodedPassword = encoder.encode(rawPassword);

        // Print the hash to console
        System.out.println("BCrypt encoded password: " + encodedPassword);
    }
}
