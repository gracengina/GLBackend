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


@Mapper(componentModel = "spring")
public interface EventMapper {
    
   
    @Mapping(target = "plannerId", source = "planner.id")
    @Mapping(target = "plannerUsername", source = "planner.username")
    @Mapping(target = "plannerFullName", source = "planner", qualifiedByName = "getPlannerFullName")
    @Mapping(target = "guestCount", source = ".", qualifiedByName = "getGuestCount")
    @Mapping(target = "vendorCount", source = ".", qualifiedByName = "getVendorCount")
    EventDTO toEventDTO(Event event);
    
    
    List<EventDTO> toEventDTOList(List<Event> events);
    

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planner", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "guests", ignore = true)
    @Mapping(target = "vendorBookings", ignore = true)
    Event toEvent(EventCreateDTO createDTO);
    
   
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "planner", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "guests", ignore = true)
    @Mapping(target = "vendorBookings", ignore = true)
    void updateEventFromDTO(EventUpdateDTO updateDTO, @MappingTarget Event event);
 
    @Named("getPlannerFullName")
    default String getPlannerFullName(com.evently.model.User planner) {
        return planner != null ? planner.getFullName() : null;
    }
    
    
    @Named("getGuestCount")
    default Integer getGuestCount(Event event) {
        return event.getGuestCount();
    }
   
    @Named("getVendorCount")
    default Integer getVendorCount(Event event) {
        return event.getVendorCount();
    }
}
