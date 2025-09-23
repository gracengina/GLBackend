package com.evently.dto.vendor;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for PortfolioItem entity responses.
 * Contains portfolio items showcasing vendor's work.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioItemDTO {
    
    private Long id;
    
    // Vendor information (simplified)
    private Long vendorId;
    private String vendorBusinessName;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    private String image; // File path/URL
    
    private LocalDateTime createdAt;
}