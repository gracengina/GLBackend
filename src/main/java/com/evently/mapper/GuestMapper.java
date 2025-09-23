package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.evently.model.Guest;
import com.evently.dto.booking.GuestCreateUpdateDTO;
import com.evently.dto.booking.GuestDTO;

/**
 * MapStruct mapper for Guest entity and DTOs.
 * Handles conversion between Guest entity and various DTOs.
 */
@Mapper(componentModel = "spring")
public interface GuestMapper {
    
    /**
     * Convert Guest entity to GuestDTO.
     * Maps event information.
     */
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    @Mapping(target = "eventDate", source = "event.date")
    GuestDTO toGuestDTO(Guest guest);
    
    /**
     * Convert list of Guest entities to list of GuestDTOs.
     */
    List<GuestDTO> toGuestDTOList(List<Guest> guests);
    
    /**
     * Convert GuestCreateUpdateDTO to Guest entity.
     * Excludes computed fields and relationships.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true) // Will be set separately
    @Mapping(target = "user", ignore = true) // Will be set separately
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Guest toGuest(GuestCreateUpdateDTO createUpdateDTO);
    
    /**
     * Update Guest entity from GuestCreateUpdateDTO.
     * Only updates modifiable fields.
     */
    // Update method for patching existing guest
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateGuestFromDTO(GuestCreateUpdateDTO updateDTO, @MappingTarget Guest guest);
}