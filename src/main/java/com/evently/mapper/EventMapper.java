package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.evently.model.Event;
import com.evently.dto.event.EventCreateDTO;
import com.evently.dto.event.EventDTO;
import com.evently.dto.event.EventUpdateDTO;

/**
 * MapStruct mapper for Event entity and DTOs.
 * Handles conversion between Event entity and various Event DTOs.
 */
@Mapper(componentModel = "spring")
public interface EventMapper {
    
    /**
     * Convert Event entity to EventDTO.
     * Maps planner information and computed fields.
     */
    @Mapping(target = "plannerId", source = "planner.id")
    @Mapping(target = "plannerUsername", source = "planner.username")
    @Mapping(target = "plannerFullName", source = "planner", qualifiedByName = "getPlannerFullName")
    @Mapping(target = "guestCount", source = ".", qualifiedByName = "getGuestCount")
    @Mapping(target = "vendorCount", source = ".", qualifiedByName = "getVendorCount")
    EventDTO toEventDTO(Event event);
    
    /**
     * Convert list of Event entities to list of EventDTOs.
     */
    List<EventDTO> toEventDTOList(List<Event> events);
    
    /**
     * Convert EventCreateDTO to Event entity.
     * Excludes computed fields and relationships.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planner", ignore = true) // Will be set from security context
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "guests", ignore = true)
    @Mapping(target = "vendorBookings", ignore = true)
    Event toEvent(EventCreateDTO createDTO);
    
    /**
     * Update Event entity from EventUpdateDTO.
     * Only updates modifiable fields.
     */
    // Update method for patching existing event
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "guests", ignore = true)
    @Mapping(target = "vendorBookings", ignore = true)
    void updateEventFromDTO(EventUpdateDTO updateDTO, @MappingTarget Event event);
    
    /**
     * Custom method to get planner full name.
     */
    @Named("getPlannerFullName")
    default String getPlannerFullName(com.evently.model.User planner) {
        return planner != null ? planner.getFullName() : null;
    }
    
    /**
     * Custom method to get guest count.
     */
    @Named("getGuestCount")
    default Integer getGuestCount(Event event) {
        return event.getGuestCount();
    }
    
    /**
     * Custom method to get vendor count.
     */
    @Named("getVendorCount")
    default Integer getVendorCount(Event event) {
        return event.getVendorCount();
    }
}