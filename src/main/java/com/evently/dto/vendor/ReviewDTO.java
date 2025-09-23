package com.evently.dto.vendor;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Review entity responses.
 * Contains customer reviews for vendors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    
    private Long id;
    
    // Vendor information (simplified)
    private Long vendorId;
    private String vendorBusinessName;
    
    // Customer information (simplified)
    private Long customerId;
    private String customerUsername;
    private String customerFullName;
    
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;
    
    @NotBlank(message = "Review comment is required")
    @Size(max = 1000, message = "Comment must not exceed 1000 characters")
    private String comment;
    
    private LocalDateTime createdAt;
}