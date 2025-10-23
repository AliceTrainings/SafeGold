package com.goldloan.safegold1.config;

import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // Refresh user data from database to ensure we have the latest information
                Optional<User> freshUser = userRepository.findById(user.getId());
                if (freshUser.isPresent()) {
                    // Update session with fresh user data
                    session.setAttribute("user", freshUser.get());
                } else {
                    // User no longer exists, invalidate session
                    session.removeAttribute("user");
                }
            }
        }
        
        return true;
    }
}
