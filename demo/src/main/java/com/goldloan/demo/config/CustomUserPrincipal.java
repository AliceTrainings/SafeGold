package com.goldloan.demo.config;

import com.goldloan.demo.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserPrincipal implements UserDetails {
    private final User user;
    
    private CustomUserPrincipal(User user) {
        this.user = user;
    }
    
    public static CustomUserPrincipal create(User user) {
        return new CustomUserPrincipal(user);
    }
    
    public Long getId() {
        return user.getId();
    }
    
    public String getFullName() {
        return user.getFullName();
    }
    
    @Override
    public String getUsername() {
        return user.getUsername();
    }
    
    @Override
    public String getPassword() {
        return user.getPassword();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
