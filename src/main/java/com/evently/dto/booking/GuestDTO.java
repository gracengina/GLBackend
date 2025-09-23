package com.evently.dto.booking;

import java.time.LocalDateTime;

import com.evently.model.Guest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Guest entity responses.
 * Contains guest information for events.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestDTO {
    
    private Long id;
    
    // Event information (simplified)
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
    
    @NotBlank(message = "Guest name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Guest email is required")
    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @NotNull(message = "RSVP status is required")
    private Guest.RsvpStatus rsvpStatus;
    
    @Size(max = 500, message = "Dietary restrictions must not exceed 500 characters")
    private String dietaryRestrictions;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Helper methods
    public boolean isAttending() {
        return Guest.RsvpStatus.ATTENDING.equals(rsvpStatus);
    }
    
    public boolean hasDeclined() {
        return Guest.RsvpStatus.DECLINED.equals(rsvpStatus);
    }
    
    public boolean isInvited() {
        return Guest.RsvpStatus.INVITED.equals(rsvpStatus);
    }
    
    public boolean isOnWaitlist() {
        return Guest.RsvpStatus.WAITLIST.equals(rsvpStatus);
    }
}