package com.evently.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.User;

/**
 * Repository for User entity.
 * Provides Spring Data JPA query methods for user operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Django User.objects.get(username=username)
    Optional<User> findByUsername(String username);
    
    // Django User.objects.get(email=email)
    Optional<User> findByEmail(String email);
    
    // Django User.objects.filter(is_vendor=True)
    List<User> findByIsVendorTrue();
    
    // Django User.objects.filter(is_planner=True)
    List<User> findByIsPlannerTrue();
    
    // Django User.objects.filter(is_active=True)
    List<User> findByIsActiveTrue();
    
    // Check if username exists (for validation)
    boolean existsByUsername(String username);
    
    // Check if email exists (for validation)
    boolean existsByEmail(String email);
    
    // Find users by partial username or email (for search)
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<User> findBySearchTerm(@Param("search") String searchTerm);
    
    // Find active vendors
    @Query("SELECT u FROM User u WHERE u.isVendor = true AND u.isActive = true")
    List<User> findActiveVendors();
    
    // Find active planners
    @Query("SELECT u FROM User u WHERE u.isPlanner = true AND u.isActive = true")
    List<User> findActivePlanners();
}