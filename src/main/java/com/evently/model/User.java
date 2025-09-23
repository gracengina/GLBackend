package com.evently.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * User entity converted from Django's AbstractUser.
 * Implements Spring Security UserDetails for authentication.
 * 
 * Maps to Django table: auth_user with additional fields
 */
@Entity
@Table(name = "users_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Django AbstractUser fields
    @Column(name = "username", unique = true, nullable = false, length = 150)
    private String username;
    
    @Column(name = "password", nullable = false, length = 128)
    private String password;
    
    @Column(name = "first_name", length = 150)
    private String firstName;
    
    @Column(name = "last_name", length = 150)
    private String lastName;
    
    @Column(name = "email", unique = true, length = 254)
    private String email;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "date_joined")
    private LocalDateTime dateJoined;
    
    // Custom fields from Django User model
    @Column(name = "is_vendor")
    private Boolean isVendor = false;
    
    @Column(name = "is_planner")
    private Boolean isPlanner = false;
    
    // Spring Security UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        if (isVendor != null && isVendor) {
            authorities.add(new SimpleGrantedAuthority("ROLE_VENDOR"));
        }
        if (isPlanner != null && isPlanner) {
            authorities.add(new SimpleGrantedAuthority("ROLE_PLANNER"));
        }
        
        // Default user role
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        return authorities;
    }
    
    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(isActive);
    }
    
    // Helper methods
    public String getFullName() {
        StringBuilder fullName = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty()) {
            fullName.append(firstName.trim());
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            if (fullName.length() > 0) {
                fullName.append(" ");
            }
            fullName.append(lastName.trim());
        }
        return fullName.length() > 0 ? fullName.toString() : username;
    }
    
    // Pre-persist to set default values
    @PrePersist
    protected void onCreate() {
        if (dateJoined == null) {
            dateJoined = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (isVendor == null) {
            isVendor = false;
        }
        if (isPlanner == null) {
            isPlanner = false;
        }
    }
    
    // Pre-update to handle entity updates
    @PreUpdate
    protected void onUpdate() {
        // Update last login time when user data is modified
        // This can be customized based on specific update scenarios
    }
    
    @Override
    public String toString() {
        return username;
    }
}