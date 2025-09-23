package com.evently.dto.vendor;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for VendorProfile entity responses.
 * Contains vendor profile information with related services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorProfileDTO {
    
    private Long id;
    
    // User information (simplified)
    private Long userId;
    private String username;
    private String fullName;
    private String email;
    
    @NotBlank(message = "Business name is required")
    @Size(max = 255, message = "Business name must not exceed 255 characters")
    private String businessName;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
    
    @Size(max = 255, message = "Contact info must not exceed 255 characters")
    private String contactInfo;
    
    private String profilePic;
    
    private Boolean isVerified;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Related data
    private List<ServiceDTO> services;
    private List<PortfolioItemDTO> portfolioItems;
    private List<ReviewDTO> reviews;
    
    // Computed fields
    private Integer servicesCount;
    private Integer portfolioCount;
    private Integer reviewsCount;
    private Double averageRating;
    
    // Helper method to get display name
    public String getDisplayName() {
        return businessName != null ? businessName : fullName;
    }
}