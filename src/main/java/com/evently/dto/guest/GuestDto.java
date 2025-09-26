package com.evently.dto.guest;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * DTO returned when reading Guest data.
 */
@Data
public class GuestDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String dietaryRestrictions;
    private String rsvpStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Event info for convenience
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
}
