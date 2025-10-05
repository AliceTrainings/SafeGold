package com.goldloan.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.Optional;

/**
 * Utility class for security-related operations
 */
public class SecurityUtils {
    
    /**
     * Get the currently authenticated user
     */
    public static Optional<CustomUserPrincipal> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserPrincipal) {
            return Optional.of((CustomUserPrincipal) authentication.getPrincipal());
        }
        
        return Optional.empty();
    }
    
    /**
     * Get the current user's ID
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentUser().map(CustomUserPrincipal::getId);
    }
    
    /**
     * Get the current username
     */
    public static Optional<String> getCurrentUsername() {
        return getCurrentUser().map(CustomUserPrincipal::getUsername);
    }
    
    /**
     * Check if user is authenticated
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !(authentication.getPrincipal() instanceof String);
    }
    
    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(String role) {
        return getCurrentUser()
            .map(user -> user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + role)))
            .orElse(false);
    }
    
    /**
     * Check if current user is admin
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }
}
