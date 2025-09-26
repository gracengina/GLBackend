package com.evently.service;

import org.springframework.stereotype.Service;

import com.evently.dto.guest.GuestCreateUpdateDTO;
import com.evently.model.Event;
import com.evently.model.Guest;
import com.evently.repository.EventRepository;
import com.evently.repository.GuestRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GuestService {

    private final GuestRepository guestRepository;
    private final EventRepository eventRepository;

    public Guest createGuest(GuestCreateUpdateDTO dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));

        Guest guest = new Guest();
        guest.setEvent(event);
        guest.setName(dto.getName());
        guest.setEmail(dto.getEmail());
        guest.setPhone(dto.getPhone());
        guest.setDietaryRestrictions(dto.getDietaryRestrictions());
        // rsvpStatus defaults to INVITED in entity
        return guestRepository.save(guest);
    }
}
