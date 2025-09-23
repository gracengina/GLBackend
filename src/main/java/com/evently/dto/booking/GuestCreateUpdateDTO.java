package com.evently.dto.booking;

import com.evently.model.Guest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating guest information.
 * Contains fields for guest management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuestCreateUpdateDTO {
    
    @NotBlank(message = "Guest name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Guest email is required")
    @Email(message = "Email must be valid")
    @Size(max = 254, message = "Email must not exceed 254 characters")
    private String email;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    private Guest.RsvpStatus rsvpStatus = Guest.RsvpStatus.INVITED; // Default status
    
    @Size(max = 500, message = "Dietary restrictions must not exceed 500 characters")
    private String dietaryRestrictions;
}