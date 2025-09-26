package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.evently.model.Guest;
import com.evently.dto.guest.GuestCreateUpdateDTO;
import com.evently.dto.guest.GuestDto;

/**
 * MapStruct mapper for Guest entity and DTOs.
 */
@Mapper(componentModel = "spring")
public interface GuestMapper {

    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    @Mapping(target = "eventDate", source = "event.date")
    GuestDto toGuestDTO(Guest guest);

    List<GuestDto> toGuestDTOList(List<Guest> guests);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rsvpStatus", ignore = true)
    Guest toGuest(GuestCreateUpdateDTO guestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "rsvpStatus", ignore = true)
    void updateGuestFromDTO(GuestCreateUpdateDTO dto, @MappingTarget Guest guest);
}
