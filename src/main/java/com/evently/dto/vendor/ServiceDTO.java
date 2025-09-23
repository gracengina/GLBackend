package com.evently.dto.vendor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Service entity responses.
 * Contains service information offered by vendors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDTO {
    
    private Long id;
    
    // Vendor information (simplified)
    private Long vendorId;
    private String vendorBusinessName;
    
    // Category information (simplified)
    private Long categoryId;
    private String categoryName;
    
    @NotBlank(message = "Service title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotBlank(message = "Service description is required")
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Size(max = 1000, message = "Availability notes must not exceed 1000 characters")
    private String availabilityNotes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}