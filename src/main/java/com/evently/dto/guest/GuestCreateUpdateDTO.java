package com.evently.dto.guest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for creating or updating a Guest.
 */
@Data
public class GuestCreateUpdateDTO {

    @NotBlank
    private String name;

    @NotBlank
    @Email
    private String email;

    @Size(max = 20)
    private String phone;

    private String dietaryRestrictions;

    private Long eventId;
}
