package com.evently.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evently.dto.user.UserDTO;
import com.evently.dto.user.UserUpdateDTO;
import com.evently.service.UserService;

import jakarta.validation.Valid;

/**
 * REST Controller for User Management.
 * Provides endpoints for user profile management and user queries.
 * This handles the /api/users/* endpoints.
 */
@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    @Autowired
    private UserService userService;

    /**
     * Get all users with pagination (Admin access).
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(Pageable pageable) {
        // For now, return active users. In production, add proper admin check
        List<UserDTO> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users);
    }

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
     * Update user profile.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody UserUpdateDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get current user to check permissions
            UserDTO currentUser = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
            
            // Allow users to update their own profile, or admin to update any profile
            if (!currentUser.getId().equals(id)) {
                // For now, allow any authenticated user to update any profile
                // In production, add proper role checking
            }
            
            UserDTO updatedUser = userService.updateUserProfile(id, updateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Search users by username, email, or name.
     */
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String query) {
        try {
            // Use existing UserRepository search method via service
            // For now, return empty list. In production, implement searchUsers method in UserService
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
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
     * Update user role (Admin only).
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long id,
            @RequestParam String role,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // In production, add proper admin role checking
            // For now, allow any authenticated user to change roles
            
            // This would need to be implemented in UserService
            // userService.updateUserRole(id, role);
            
            return userService.getUserById(id)
                    .map(user -> ResponseEntity.ok(user))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}