package com.evently.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.Review;
import com.evently.model.User;
import com.evently.model.VendorProfile;


@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find reviews by vendor
    List<Review> findByVendor(VendorProfile vendor);
    
    List<Review> findByVendorId(Long vendorId);
    
    // Find reviews by user
    List<Review> findByUser(User user);
    
    List<Review> findByUserId(Long userId);
    
    // Find reviews by rating
    List<Review> findByRating(Integer rating);
    
    List<Review> findByRatingGreaterThanEqual(Integer minRating);
    
    List<Review> findByRatingLessThanEqual(Integer maxRating);
    
    List<Review> findByRatingBetween(Integer minRating, Integer maxRating);
    
    // Find reviews by vendor and rating
    List<Review> findByVendorAndRating(VendorProfile vendor, Integer rating);
    
    List<Review> findByVendorAndRatingGreaterThanEqual(VendorProfile vendor, Integer minRating);
    
    // Find reviews with comments
    List<Review> findByCommentIsNotNull();
    
    List<Review> findByVendorAndCommentIsNotNull(VendorProfile vendor);
    
    // Find reviews ordered by creation date
    List<Review> findByVendorOrderByCreatedAtDesc(VendorProfile vendor);
    
    List<Review> findByUserOrderByCreatedAtDesc(User user);
    
    // Custom queries
    @Query("SELECT r FROM Review r WHERE r.comment LIKE %:keyword%")
    List<Review> findByCommentContaining(@Param("keyword") String keyword);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vendor = :vendor")
    Double getAverageRatingByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.vendor = :vendor")
    long countReviewsByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.vendor = :vendor AND r.rating = :rating")
    long countReviewsByVendorAndRating(@Param("vendor") VendorProfile vendor, @Param("rating") Integer rating);
    
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.vendor = :vendor GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistributionByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT r FROM Review r WHERE r.vendor = :vendor ORDER BY r.rating DESC, r.createdAt DESC")
    List<Review> findTopReviewsByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT r FROM Review r WHERE r.vendor = :vendor AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    List<Review> findHighRatedReviewsByVendor(@Param("vendor") VendorProfile vendor, @Param("minRating") Integer minRating);
    
    @Query("SELECT r FROM Review r WHERE r.rating >= :minRating ORDER BY r.createdAt DESC")
    List<Review> findHighRatedReviews(@Param("minRating") Integer minRating);
    
    @Query("SELECT r FROM Review r WHERE r.vendor.isVerified = true ORDER BY r.createdAt DESC")
    List<Review> findReviewsForVerifiedVendors();
    
    @Query("SELECT r FROM Review r WHERE r.vendor.isVerified = true AND r.rating >= :minRating ORDER BY r.createdAt DESC")
    List<Review> findHighRatedReviewsForVerifiedVendors(@Param("minRating") Integer minRating);
    
    // Find recent reviews
    @Query("SELECT r FROM Review r WHERE r.createdAt >= (CURRENT_TIMESTAMP - :days DAY) ORDER BY r.createdAt DESC")
    List<Review> findRecentReviews(@Param("days") int days);
    
    // Check if user has reviewed vendor
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.vendor = :vendor AND r.user = :user")
    boolean hasUserReviewedVendor(@Param("vendor") VendorProfile vendor, @Param("user") User user);
    
    // Find vendors with highest average ratings
    @Query("SELECT r.vendor, AVG(r.rating) FROM Review r GROUP BY r.vendor HAVING COUNT(r) >= :minReviews ORDER BY AVG(r.rating) DESC")
    List<Object[]> findVendorsWithHighestRatings(@Param("minReviews") long minReviews);
    
    // Additional methods needed by VendorService
    boolean existsByVendorIdAndUserId(Long vendorId, Long userId);
    
    List<Review> findByVendorIdAndRating(Long vendorId, Integer rating);
    
    int countByVendorId(Long vendorId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vendor.id = :vendorId")
    Double findAverageRatingByVendorId(@Param("vendorId") Long vendorId);
}
