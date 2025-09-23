package com.evently.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evently.model.User;
import com.evently.dto.user.UserDTO;
import com.evently.dto.user.UserRegistrationDTO;
import com.evently.dto.user.UserUpdateDTO;
import com.evently.mapper.UserMapper;
import com.evently.repository.UserRepository;

/**
 * Service layer for User entity operations.
 * Handles user registration, authentication, and profile management.
 * Implements Spring Security UserDetailsService for authentication.
 */
@Service
@Transactional
public class UserService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Spring Security UserDetailsService implementation
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    /**
     * Register a new user account.
     */
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + registrationDTO.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + registrationDTO.getEmail());
        }
        
        // Validate password confirmation
        if (!registrationDTO.isPasswordMatching()) {
            throw new IllegalArgumentException("Password confirmation does not match");
        }
        
        // Convert DTO to entity
        User user = userMapper.toUser(registrationDTO);
        
        // Encode password
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));
        
        // Set creation timestamp
        user.setDateJoined(LocalDateTime.now());
        
        // Save user
        User savedUser = userRepository.save(user);
        
        return userMapper.toUserDTO(savedUser);
    }
    
    /**
     * Get user by ID.
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toUserDTO);
    }
    
    /**
     * Get user by username.
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toUserDTO);
    }
    
    /**
     * Get user by email.
     */
    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toUserDTO);
    }
    
    /**
     * Update user profile.
     */
    public UserDTO updateUserProfile(Long userId, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        // Check if email is being changed and if it already exists
        if (updateDTO.getEmail() != null && !updateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDTO.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + updateDTO.getEmail());
            }
        }
        
        // Update user fields
        userMapper.updateUserFromDTO(updateDTO, user);
        
        // Save updated user
        User savedUser = userRepository.save(user);
        
        return userMapper.toUserDTO(savedUser);
    }
    
    /**
     * Update last login timestamp.
     */
    public void updateLastLogin(String username) {
        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                });
    }
    
    /**
     * Get all planners.
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllPlanners() {
        List<User> planners = userRepository.findByIsPlannerTrue();
        return userMapper.toUserDTOList(planners);
    }
    
    /**
     * Get all vendors.
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllVendors() {
        List<User> vendors = userRepository.findByIsVendorTrue();
        return userMapper.toUserDTOList(vendors);
    }
    
    /**
     * Get all active users.
     */
    @Transactional(readOnly = true)
    public List<UserDTO> getAllActiveUsers() {
        List<User> activeUsers = userRepository.findByIsActiveTrue();
        return userMapper.toUserDTOList(activeUsers);
    }
    
    /**
     * Deactivate user account.
     */
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        user.setIsActive(false);
        userRepository.save(user);
    }
    
    /**
     * Activate user account.
     */
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        user.setIsActive(true);
        userRepository.save(user);
    }
    
    /**
     * Check if username exists.
     */
    @Transactional(readOnly = true)
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email exists.
     */
    @Transactional(readOnly = true)
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}