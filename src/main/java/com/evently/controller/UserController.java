package com.evently.controller;

import java.util.List;

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
    
    // Authentication Endpoints
    
    /**
     * Register a new user.
     */
    @PostMapping("/register")
public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
    try {
        UserDTO user = userService.registerUser(registrationDTO);

        // Generate JWT for the new user
        String token = jwtTokenUtil.generateToken(user.getUsername()); // replace with your JWT method

        // Build a response with user and token
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().build();
    }
}
    
    /**
     * Authenticate user login.
     * Note: This is a simplified version. In a real application, Spring Security would handle authentication.
     */
    @PostMapping("/login")
    public ResponseEntity<UserDTO> authenticateUser(@Valid @RequestBody UserLoginDTO loginDTO) {
        try {
            // This would typically be handled by Spring Security
            return userService.getUserByUsername(loginDTO.getUsername())
                    .map(user -> ResponseEntity.ok(user))
                    .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    // Profile Management
    
    /**
     * Get current user profile.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByUsername(userDetails.getUsername())
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get user by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
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
     * Note: UserService doesn't have deleteUser method, so this is not implemented.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        // UserService doesn't have delete functionality
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    // Query Endpoints
    
    /**
     * Get all users with pagination.
     * Note: UserService doesn't have getAllUsers method with pagination.
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        // Since there's no paginated method, return active users as an alternative
        List<UserDTO> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }
    
    /**
     * Search users by term.
     * Note: UserService doesn't have searchUsers method.
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
        // Since search method doesn't exist, return all active users
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
    
    // Role Management
    
    /**
     * Set user as planner.
     * Note: UserService doesn't have setUserAsPlanner method.
     */
    @PutMapping("/{id}/make-planner")
    public ResponseEntity<Void> makeUserPlanner(@PathVariable Long id) {
        // UserService doesn't have role management methods
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
    
    /**
     * Set user as vendor.
     * Note: UserService doesn't have setUserAsVendor method.
     */
    @PutMapping("/{id}/make-vendor")
    public ResponseEntity<Void> makeUserVendor(@PathVariable Long id) {
        // UserService doesn't have role management methods
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
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
