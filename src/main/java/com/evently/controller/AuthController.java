package com.evently.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evently.dto.user.UserDTO;
import com.evently.dto.user.UserLoginDTO;
import com.evently.dto.user.UserRegistrationDTO;
import com.evently.security.JwtTokenProvider;
import com.evently.service.UserService;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.HashMap;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Authentication Controller.
 * Handles user registration, login, and JWT token management.
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowCredentials = "true")  // Allow all origins
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user and automatically log them in.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            // Register the user
            UserDTO user = userService.registerUser(registrationDTO);
            
            // Automatically authenticate the new user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    registrationDTO.getUsername(),
                    registrationDTO.getPassword()
                )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            String jwt = jwtTokenProvider.generateToken(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            return ResponseEntity.status(HttpStatus.CREATED).body(new RegistrationResponse(
                jwt,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                userDetails.getAuthorities().toString(),
                "Registration successful"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Registration failed", e.getMessage()));
        } catch (Exception e) {
            // Log the full exception for debugging
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Registration failed", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Authenticate user and return JWT token.
     */
   @PostMapping("/login")
public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserLoginDTO loginDTO) {
    try {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(),
                loginDTO.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Map<String, Object> body = new HashMap<>();
        body.put("token", jwt);
        body.put("type", "Bearer");
        body.put("username", userDetails.getUsername());
        body.put("roles", userDetails.getAuthorities());

        return ResponseEntity.ok(body);

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Authentication failed", 
                             "message", "Invalid username or password"));
    }
}

    /**
     * Get current user profile.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Unauthorized", "Authentication required"));
            }

            String username = authentication.getName();
            UserDTO user = userService.getUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("User not found", e.getMessage()));
        }
    }

    // Response classes
    public static class RegistrationResponse {
        private String token;
        private String type;
        private Long id;
        private String username;
        private String email;
        private String authorities;
        private String message;

        public RegistrationResponse(String token, String type, Long id, String username, String email, String authorities, String message) {
            this.token = token;
            this.type = type;
            this.id = id;
            this.username = username;
            this.email = email;
            this.authorities = authorities;
            this.message = message;
        }

        // Getters
        public String getToken() { return token; }
        public String getType() { return type; }
        public Long getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getAuthorities() { return authorities; }
        public String getMessage() { return message; }
    }

    public static class JwtResponse {
        private String token;
        private String type;
        private String username;
        private String authorities;

        public JwtResponse(String token, String type, String username, String authorities) {
            this.token = token;
            this.type = type;
            this.username = username;
            this.authorities = authorities;
        }

        // Getters
        public String getToken() { return token; }
        public String getType() { return type; }
        public String getUsername() { return username; }
        public String getAuthorities() { return authorities; }
    }

    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        // Getters
        public String getError() { return error; }
        public String getMessage() { return message; }
    }
}
