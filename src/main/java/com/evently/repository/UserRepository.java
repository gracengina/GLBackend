package com.evently.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
   
    Optional<User> findByUsername(String username);
    
    
    Optional<User> findByEmail(String email);
    
    List<User> findByIsVendorTrue();
    
  
    List<User> findByIsPlannerTrue();
    
    
    List<User> findByIsActiveTrue();
    
    
    boolean existsByUsername(String username);
    
    
    boolean existsByEmail(String email);
    
    
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
