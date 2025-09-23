package com.evently.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.PortfolioItem;
import com.evently.model.VendorProfile;

/**
 * Repository interface for PortfolioItem entity operations.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    
    // Find portfolio items by vendor
    List<PortfolioItem> findByVendor(VendorProfile vendor);
    
    List<PortfolioItem> findByVendorId(Long vendorId);
    
    // Find portfolio items by vendor ordered by creation date
    List<PortfolioItem> findByVendorOrderByCreatedAtDesc(VendorProfile vendor);
    
    List<PortfolioItem> findByVendorOrderByCreatedAtAsc(VendorProfile vendor);
    
    // Find portfolio items with images
    List<PortfolioItem> findByImageIsNotNull();
    
    // Find portfolio items without images
    List<PortfolioItem> findByImageIsNull();
    
    // Find portfolio items by vendor with images
    List<PortfolioItem> findByVendorAndImageIsNotNull(VendorProfile vendor);
    
    // Find portfolio items by description
    List<PortfolioItem> findByDescriptionContainingIgnoreCase(String description);
    
    // Custom queries
    @Query("SELECT pi FROM PortfolioItem pi WHERE pi.vendor = :vendor AND pi.image IS NOT NULL ORDER BY pi.createdAt DESC")
    List<PortfolioItem> findLatestPortfolioWithImagesByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT COUNT(pi) FROM PortfolioItem pi WHERE pi.vendor = :vendor")
    long countPortfolioItemsByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT COUNT(pi) FROM PortfolioItem pi WHERE pi.vendor = :vendor AND pi.image IS NOT NULL")
    long countPortfolioItemsWithImagesByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT pi FROM PortfolioItem pi WHERE pi.vendor.isVerified = true ORDER BY pi.createdAt DESC")
    List<PortfolioItem> findLatestPortfolioFromVerifiedVendors();
    
    @Query("SELECT pi FROM PortfolioItem pi WHERE pi.vendor.isVerified = true AND pi.image IS NOT NULL ORDER BY pi.createdAt DESC")
    List<PortfolioItem> findLatestPortfolioWithImagesFromVerifiedVendors();
    
    // Find portfolio items by vendor location
    @Query("SELECT pi FROM PortfolioItem pi WHERE pi.vendor.location LIKE %:location%")
    List<PortfolioItem> findByVendorLocation(@Param("location") String location);
    
    // Find recent portfolio items (last N days)
    @Query("SELECT pi FROM PortfolioItem pi WHERE pi.createdAt >= (CURRENT_TIMESTAMP - :days DAY) ORDER BY pi.createdAt DESC")
    List<PortfolioItem> findRecentPortfolioItems(@Param("days") int days);
    
    // Find portfolio by vendor business name
    @Query("SELECT pi FROM PortfolioItem pi WHERE pi.vendor.businessName LIKE %:businessName%")
    List<PortfolioItem> findByVendorBusinessName(@Param("businessName") String businessName);
    
    // Additional methods needed by VendorService
    int countByVendorId(Long vendorId);
}