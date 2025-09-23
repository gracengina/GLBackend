package com.evently.dto.booking;

import java.time.LocalDateTime;

import com.evently.model.VendorBooking.BookingStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for VendorBooking entity responses.
 * Contains booking information for vendor services.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorBookingDTO {
    
    private Long id;
    
    // Event information (simplified)
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
    private String eventLocation;
    
    // Vendor information (simplified)
    private Long vendorId;
    private String vendorBusinessName;
    
    // Service information (simplified)
    private Long serviceId;
    private String serviceTitle;
    private String servicePrice;
    
    @NotNull(message = "Booking status is required")
    private BookingStatus status;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Helper methods
    public boolean isPending() {
        return BookingStatus.PENDING.equals(status);
    }
    
    public boolean isConfirmed() {
        return BookingStatus.CONFIRMED.equals(status);
    }
    
    public boolean isCancelled() {
        return BookingStatus.CANCELLED.equals(status);
    }
    
    public boolean isCompleted() {
        return BookingStatus.COMPLETED.equals(status);
    }
}