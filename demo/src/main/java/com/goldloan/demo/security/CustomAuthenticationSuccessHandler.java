package com.goldloan.demo.security;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Custom authentication success handler
 * Redirects users to different pages based on their roles after successful login
 */
@Component
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        log.info("User authenticated successfully: {}", authentication.getName());
        
        // Get the user principal
        CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
        
        // Clear any existing authentication exception attributes
        clearAuthenticationAttributes(request);
        
        // Log user login
        log.info("User '{}' logged in with role: {}", userPrincipal.getUsername(), userPrincipal.getRole());
        
        // Determine redirect URL based on role
        String targetUrl = determineTargetUrl(authentication);
        
        log.debug("Redirecting user to: {}", targetUrl);
        
        // Redirect to target URL
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }
    
    /**
     * Determine the target URL based on user's role
     */
    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        for (GrantedAuthority grantedAuthority : authorities) {
            String authority = grantedAuthority.getAuthority();
            
            if (authority.equals("ROLE_ADMIN")) {
                log.debug("User has ADMIN role, redirecting to admin dashboard");
                return "/admin/dashboard";
            } else if (authority.equals("ROLE_USER")) {
                log.debug("User has USER role, redirecting to user dashboard");
                return "/user/dashboard";
            }
        }
        
        // Default redirect for users without specific roles
        log.debug("User has no specific role, redirecting to homepage");
        return "/";
    }
    
    /**
     * Clear authentication exception attributes from session
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
