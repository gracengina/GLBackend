package com.evently.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user profile update requests.
 * Contains fields that can be updated by the user.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
    @Size(max = 150, message = "First name must not exceed 150 characters")
    private String firstName;
    
    @Size(max = 150, message = "Last name must not exceed 150 characters")
    private String lastName;
    
    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;
    
    // Users can opt in/out of vendor or planner roles
    private Boolean isVendor;
    
    private Boolean isPlanner;
}