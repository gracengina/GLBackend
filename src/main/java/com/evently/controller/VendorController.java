package com.evently.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.evently.dto.vendor.PortfolioItemDTO;
import com.evently.dto.vendor.ReviewDTO;
import com.evently.dto.vendor.ServiceDTO;
import com.evently.dto.vendor.VendorProfileCreateUpdateDTO;
import com.evently.dto.vendor.VendorProfileDTO;
import com.evently.service.UserService;
import com.evently.service.VendorService;
import com.evently.service.VendorService.VendorStatsDTO;

import jakarta.validation.Valid;

/**
 * REST Controller for Vendor management.
 * Provides endpoints for vendor profile management, service offerings, and portfolio handling.
 */
@RestController
@RequestMapping("/api/vendors")
public class VendorController {
    
    @Autowired
    private VendorService vendorService;
    
    @Autowired
    private UserService userService;
    
   
    @PostMapping("/profile")
    public ResponseEntity<VendorProfileDTO> createVendorProfile(
            @Valid @RequestBody VendorProfileCreateUpdateDTO createDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            VendorProfileDTO profile = vendorService.createVendorProfile(createDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(profile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get vendor profile by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendorProfileDTO> getVendorProfileById(@PathVariable Long id) {
        return vendorService.getVendorProfileById(id)
                .map(profile -> ResponseEntity.ok(profile))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get current user's vendor profile.
     */
    @GetMapping("/my-profile")
    public ResponseEntity<VendorProfileDTO> getMyVendorProfile(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            return vendorService.getVendorProfileByUserId(userId)
                    .map(profile -> ResponseEntity.ok(profile))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update vendor profile.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VendorProfileDTO> updateVendorProfile(
            @PathVariable Long id,
            @Valid @RequestBody VendorProfileCreateUpdateDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            VendorProfileDTO updatedProfile = vendorService.updateVendorProfile(id, updateDTO, userId);
            return ResponseEntity.ok(updatedProfile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    @GetMapping
    public ResponseEntity<Page<VendorProfileDTO>> getAllVendorProfiles(Pageable pageable) {
        Page<VendorProfileDTO> vendors = vendorService.getAllVendorProfiles(pageable);
        return ResponseEntity.ok(vendors);
    }
    
    /**
     * Get verified vendor profiles.
     */
    @GetMapping("/verified")
    public ResponseEntity<List<VendorProfileDTO>> getVerifiedVendorProfiles() {
        List<VendorProfileDTO> vendors = vendorService.getVerifiedVendorProfiles();
        return ResponseEntity.ok(vendors);
    }
    
    /**
     * Search vendor profiles.
     */
    @GetMapping("/search")
    public ResponseEntity<List<VendorProfileDTO>> searchVendorProfiles(@RequestParam String query) {
        List<VendorProfileDTO> vendors = vendorService.searchVendorProfiles(query);
        return ResponseEntity.ok(vendors);
    }
    
    /**
     * Get vendor profiles by location.
     */
    @GetMapping("/location")
    public ResponseEntity<List<VendorProfileDTO>> getVendorProfilesByLocation(@RequestParam String location) {
        List<VendorProfileDTO> vendors = vendorService.getVendorProfilesByLocation(location);
        return ResponseEntity.ok(vendors);
    }
    
 
    @PostMapping("/{vendorId}/services")
    public ResponseEntity<ServiceDTO> addServiceToVendor(
            @PathVariable Long vendorId,
            @Valid @RequestBody ServiceDTO serviceDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            ServiceDTO service = vendorService.addServiceToVendor(vendorId, serviceDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(service);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get services by vendor.
     */
    @GetMapping("/{vendorId}/services")
    public ResponseEntity<List<ServiceDTO>> getServicesByVendor(@PathVariable Long vendorId) {
        List<ServiceDTO> services = vendorService.getServicesByVendor(vendorId);
        return ResponseEntity.ok(services);
    }
    
    /**
     * Update service.
     */
    @PutMapping("/services/{serviceId}")
    public ResponseEntity<ServiceDTO> updateService(
            @PathVariable Long serviceId,
            @Valid @RequestBody ServiceDTO serviceDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            ServiceDTO updatedService = vendorService.updateService(serviceId, serviceDTO, userId);
            return ResponseEntity.ok(updatedService);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete service.
     */
    @DeleteMapping("/services/{serviceId}")
    public ResponseEntity<Void> deleteService(
            @PathVariable Long serviceId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            vendorService.deleteService(serviceId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get services by category.
     */
    @GetMapping("/services/category/{categoryId}")
    public ResponseEntity<List<ServiceDTO>> getServicesByCategory(@PathVariable Long categoryId) {
        List<ServiceDTO> services = vendorService.getServicesByCategory(categoryId);
        return ResponseEntity.ok(services);
    }
    
    
    @PostMapping("/{vendorId}/portfolio")
    public ResponseEntity<PortfolioItemDTO> addPortfolioItem(
            @PathVariable Long vendorId,
            @Valid @RequestBody PortfolioItemDTO portfolioItemDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            PortfolioItemDTO portfolioItem = vendorService.addPortfolioItem(vendorId, portfolioItemDTO, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(portfolioItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get portfolio items by vendor.
     */
    @GetMapping("/{vendorId}/portfolio")
    public ResponseEntity<List<PortfolioItemDTO>> getPortfolioItemsByVendor(@PathVariable Long vendorId) {
        List<PortfolioItemDTO> items = vendorService.getPortfolioItemsByVendor(vendorId);
        return ResponseEntity.ok(items);
    }
    
    /**
     * Update portfolio item.
     */
    @PutMapping("/portfolio/{itemId}")
    public ResponseEntity<PortfolioItemDTO> updatePortfolioItem(
            @PathVariable Long itemId,
            @Valid @RequestBody PortfolioItemDTO portfolioItemDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            PortfolioItemDTO updatedItem = vendorService.updatePortfolioItem(itemId, portfolioItemDTO, userId);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete portfolio item.
     */
    @DeleteMapping("/portfolio/{itemId}")
    public ResponseEntity<Void> deletePortfolioItem(
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            vendorService.deletePortfolioItem(itemId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
   
    @PostMapping("/{vendorId}/reviews")
    public ResponseEntity<ReviewDTO> addReviewForVendor(
            @PathVariable Long vendorId,
            @Valid @RequestBody ReviewDTO reviewDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long customerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            ReviewDTO review = vendorService.addReviewForVendor(vendorId, reviewDTO, customerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get reviews by vendor.
     */
    @GetMapping("/{vendorId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByVendor(@PathVariable Long vendorId) {
        List<ReviewDTO> reviews = vendorService.getReviewsByVendor(vendorId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Get reviews by vendor and rating.
     */
    @GetMapping("/{vendorId}/reviews/rating/{rating}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByVendorAndRating(
            @PathVariable Long vendorId,
            @PathVariable Integer rating) {
        List<ReviewDTO> reviews = vendorService.getReviewsByVendorAndRating(vendorId, rating);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Update review.
     */
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDTO reviewDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long customerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            ReviewDTO updatedReview = vendorService.updateReview(reviewId, reviewDTO, customerId);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete review.
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long customerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            vendorService.deleteReview(reviewId, customerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    
    @GetMapping("/{vendorId}/stats")
    public ResponseEntity<VendorStatsDTO> getVendorStatistics(@PathVariable Long vendorId) {
        try {
            VendorStatsDTO stats = vendorService.getVendorStatistics(vendorId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
