package com.evently.dto.vendor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating VendorProfile.
 * Contains fields that can be modified by vendors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorProfileCreateUpdateDTO {
    
    @NotBlank(message = "Business name is required")
    @Size(max = 255, message = "Business name must not exceed 255 characters")
    private String businessName;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
    
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
    
    @Size(max = 255, message = "Contact info must not exceed 255 characters")
    private String contactInfo;
    
}
