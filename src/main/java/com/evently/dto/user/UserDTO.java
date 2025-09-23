package com.evently.dto.user;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for User entity responses.
 * Contains safe user information without sensitive data like password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 150, message = "Username must be between 3 and 150 characters")
    private String username;
    
    @Size(max = 150, message = "First name must not exceed 150 characters")
    private String firstName;
    
    @Size(max = 150, message = "Last name must not exceed 150 characters")
    private String lastName;
    
    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;
    
    private Boolean isActive;
    
    private LocalDateTime lastLogin;
    
    private LocalDateTime dateJoined;
    
    private Boolean isVendor;
    
    private Boolean isPlanner;
    
    // Computed field
    private String fullName;
    
    // Helper method to get display name
    public String getDisplayName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        return username;
    }
}