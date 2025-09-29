package com.evently.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 * Contains credentials for authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 150, message = "Username must be between 3 and 150 characters")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // remember me functionality
    private Boolean rememberMe = false;
}
