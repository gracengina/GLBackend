package com.evently.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.Service;
import com.evently.model.ServiceCategory;
import com.evently.model.VendorProfile;


@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    // Find services by vendor
    List<Service> findByVendor(VendorProfile vendor);
    
    List<Service> findByVendorId(Long vendorId);
    
    // Find services by category
    List<Service> findByCategory(ServiceCategory category);
    
    List<Service> findByCategoryId(Long categoryId);
    
    // Find services by title
    List<Service> findByTitleContainingIgnoreCase(String title);
    
    // Find services by price range
    List<Service> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    List<Service> findByPriceLessThanEqual(BigDecimal maxPrice);
    
    List<Service> findByPriceGreaterThanEqual(BigDecimal minPrice);
    
    // Find services by vendor and category
    List<Service> findByVendorAndCategory(VendorProfile vendor, ServiceCategory category);
    
    // Find services by vendor and price range
    List<Service> findByVendorAndPriceBetween(VendorProfile vendor, BigDecimal minPrice, BigDecimal maxPrice);
    
    // Custom queries
    @Query("SELECT s FROM Service s WHERE s.description LIKE %:keyword% OR s.title LIKE %:keyword%")
    List<Service> findByKeywordInTitleOrDescription(@Param("keyword") String keyword);
    
    @Query("SELECT s FROM Service s WHERE s.vendor.location LIKE %:location%")
    List<Service> findByVendorLocation(@Param("location") String location);
    
    @Query("SELECT s FROM Service s WHERE s.vendor.isVerified = true")
    List<Service> findByVerifiedVendors();
    
    @Query("SELECT s FROM Service s WHERE s.vendor.isVerified = true AND s.category = :category")
    List<Service> findByVerifiedVendorsAndCategory(@Param("category") ServiceCategory category);
    
    @Query("SELECT s FROM Service s WHERE s.vendor.isVerified = true AND s.price <= :maxPrice")
    List<Service> findByVerifiedVendorsAndMaxPrice(@Param("maxPrice") BigDecimal maxPrice);
    
    // Find cheapest services by category
    @Query("SELECT s FROM Service s WHERE s.category = :category ORDER BY s.price ASC")
    List<Service> findCheapestServicesByCategory(@Param("category") ServiceCategory category);
    
    // Find most expensive services by category  
    @Query("SELECT s FROM Service s WHERE s.category = :category ORDER BY s.price DESC")
    List<Service> findMostExpensiveServicesByCategory(@Param("category") ServiceCategory category);
    
    // Statistics queries
    @Query("SELECT AVG(s.price) FROM Service s WHERE s.category = :category")
    BigDecimal getAveragePriceByCategory(@Param("category") ServiceCategory category);
    
    @Query("SELECT MIN(s.price) FROM Service s WHERE s.category = :category")
    BigDecimal getMinPriceByCategory(@Param("category") ServiceCategory category);
    
    @Query("SELECT MAX(s.price) FROM Service s WHERE s.category = :category")
    BigDecimal getMaxPriceByCategory(@Param("category") ServiceCategory category);
    
    @Query("SELECT COUNT(s) FROM Service s WHERE s.vendor = :vendor")
    long countServicesByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT COUNT(s) FROM Service s WHERE s.category = :category")
    long countServicesByCategory(@Param("category") ServiceCategory category);
    
    // Find services with bookings
    @Query("SELECT DISTINCT s FROM Service s JOIN VendorBooking vb ON vb.service = s")
    List<Service> findServicesWithBookings();
    
    // Find popular services (most booked)
    @Query("SELECT s, COUNT(vb) FROM Service s JOIN VendorBooking vb ON vb.service = s GROUP BY s ORDER BY COUNT(vb) DESC")
    List<Object[]> findPopularServices();
    
    // Additional methods needed by VendorService
    int countByVendorId(Long vendorId);
}
