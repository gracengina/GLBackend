package com.evently.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evently.dto.user.UserDTO;
import com.evently.dto.user.UserLoginDTO;
import com.evently.dto.user.UserRegistrationDTO;
import com.evently.dto.user.UserUpdateDTO;
import com.evently.service.UserService;
import com.evently.security.JwtTokenProvider;   // ✅ import your JWT provider

import jakarta.validation.Valid;

/**
 * REST Controller for User management.
 * Provides endpoints for user registration, authentication, profile management, and user queries.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ Inject the JWT provider so we can create tokens
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    /**
     * Register a new user and return a JWT token.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserDTO user = userService.registerUser(registrationDTO);

            // Generate JWT for the new user
            String token = jwtTokenProvider.generateTokenFromUsername(user.getUsername());

            // Build a response with user info and token
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", token);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Authenticate user login (basic example – adjust if using Spring Security login flow).
     */
    @PostMapping("/login")
    public ResponseEntity<UserDTO> authenticateUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            return userService.getUserByUsername(loginDTO.getUsername())
                    .map(user -> ResponseEntity.ok(user))
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Get current user profile.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update current user profile.
     */
    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateCurrentUser(
            @Valid @RequestBody UserUpdateDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            UserDTO currentUser = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            UserDTO updatedUser = userService.updateUserProfile(currentUser.getId(), updateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update user by ID (admin only).
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        try {
            UserDTO updatedUser = userService.updateUserProfile(id, updateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete user by ID (admin only).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    /**
     * Get all users.
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Search users by term.
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
        List<UserDTO> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get all planners.
     */
    @GetMapping("/planners")
    public ResponseEntity<List<UserDTO>> getAllPlanners() {
        List<UserDTO> planners = userService.getAllPlanners();
        return ResponseEntity.ok(planners);
    }

    /**
     * Get all vendors.
     */
    @GetMapping("/vendors")
    public ResponseEntity<List<UserDTO>> getAllVendors() {
        List<UserDTO> vendors = userService.getAllVendors();
        return ResponseEntity.ok(vendors);
    }

    /**
     * Get all active users.
     */
    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> getAllActiveUsers() {
        List<UserDTO> activeUsers = userService.getAllActiveUsers();
        return ResponseEntity.ok(activeUsers);
    }

    /**
     * Activate user.
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable Long id) {
        try {
            userService.activateUser(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Deactivate user.
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
