package com.goldloan.demo.security;

import com.goldloan.demo.entity.Role;
import com.goldloan.demo.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Custom UserDetails implementation that wraps our User entity
 * This is what Spring Security will use throughout the application
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserPrincipal implements UserDetails {

    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String phoneNumber;

    @JsonIgnore
    private String password;

    private Role role;

    private boolean enabled;

    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Factory method to create CustomUserPrincipal from User entity
     */
    public static CustomUserPrincipal create(User user) {
        Collection<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().getName())
        );

        return new CustomUserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getFullName(),
            user.getPhoneNumber(),
            user.getPassword(),
            user.getRole(),
            user.isEnabled(),
            authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return enabled;
    }

    /**
     * Check if user has a specific role
     */
    public boolean hasRole(Role role) {
        return this.role.equals(role);
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return this.role.getName().equalsIgnoreCase("ADMIN");
    }

    /**
     * Check if user is regular user
     */
    public boolean isUser() {
        return this.role.getName().equalsIgnoreCase("USER");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserPrincipal that = (CustomUserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
