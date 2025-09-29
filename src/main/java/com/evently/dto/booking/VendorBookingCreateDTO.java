package com.evently.dto.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating vendor bookings.
 * Contains required fields for booking a vendor service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorBookingCreateDTO {
    
    @NotNull(message = "Event ID is required")
    private Long eventId;
    
    @NotNull(message = "Vendor ID is required")
    private Long vendorId;
    
    @NotNull(message = "Service ID is required")
    private Long serviceId;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    
}
