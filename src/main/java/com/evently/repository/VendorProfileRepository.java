package com.evently.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.User;
import com.evently.model.VendorProfile;

/**
 * Repository interface for VendorProfile entity operations.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface VendorProfileRepository extends JpaRepository<VendorProfile, Long> {
    
    // Find vendor by user
    Optional<VendorProfile> findByUser(User user);
    
    Optional<VendorProfile> findByUserId(Long userId);
    
    // Find vendors by business name
    List<VendorProfile> findByBusinessNameContainingIgnoreCase(String businessName);
    
    // Find vendors by location
    List<VendorProfile> findByLocationContainingIgnoreCase(String location);
    
    // Find verified vendors
    List<VendorProfile> findByIsVerified(Boolean isVerified);
    
    List<VendorProfile> findByIsVerifiedTrue();
    
    // Find vendors by business name and verification status
    List<VendorProfile> findByBusinessNameContainingIgnoreCaseAndIsVerified(String businessName, Boolean isVerified);
    
    // Find vendors by location and verification status
    List<VendorProfile> findByLocationContainingIgnoreCaseAndIsVerified(String location, Boolean isVerified);
    
    // Custom queries
    @Query("SELECT vp FROM VendorProfile vp WHERE vp.description LIKE %:keyword% OR vp.businessName LIKE %:keyword%")
    List<VendorProfile> findByKeywordInBusinessNameOrDescription(@Param("keyword") String keyword);
    
    @Query("SELECT vp FROM VendorProfile vp WHERE vp.isVerified = true ORDER BY vp.createdAt DESC")
    List<VendorProfile> findVerifiedVendorsOrderByCreatedDesc();
    
    @Query("SELECT COUNT(vp) FROM VendorProfile vp WHERE vp.isVerified = true")
    long countVerifiedVendors();
    
    @Query("SELECT COUNT(vp) FROM VendorProfile vp WHERE vp.isVerified = false")
    long countUnverifiedVendors();
    
    // Find vendors with reviews
    @Query("SELECT DISTINCT vp FROM VendorProfile vp JOIN vp.reviews r")
    List<VendorProfile> findVendorsWithReviews();
    
    // Find vendors with high ratings (4+ stars average)
    @Query("SELECT vp FROM VendorProfile vp JOIN vp.reviews r GROUP BY vp HAVING AVG(r.rating) >= :minRating")
    List<VendorProfile> findVendorsWithMinAverageRating(@Param("minRating") Double minRating);
    
    // Find vendors with services in specific category
    @Query("SELECT DISTINCT vp FROM VendorProfile vp JOIN vp.services s WHERE s.category.name = :categoryName")
    List<VendorProfile> findVendorsByCategoryName(@Param("categoryName") String categoryName);
    
    // Check if user has vendor profile
    @Query("SELECT CASE WHEN COUNT(vp) > 0 THEN true ELSE false END FROM VendorProfile vp WHERE vp.user = :user")
    boolean hasVendorProfile(@Param("user") User user);
    
    // Additional methods needed by VendorService
    boolean existsByUserId(Long userId);
    
    List<VendorProfile> findByBusinessNameContainingIgnoreCaseOrLocationContainingIgnoreCase(String businessName, String location);
}