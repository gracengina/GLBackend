package com.evently.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evently.model.Event;
import com.evently.model.Guest;
import com.evently.model.User;
import com.evently.dto.event.EventCreateDTO;
import com.evently.dto.event.EventDTO;
import com.evently.dto.event.EventUpdateDTO;
import com.evently.dto.booking.GuestCreateUpdateDTO;
import com.evently.dto.booking.GuestDTO;
import com.evently.mapper.EventMapper;
import com.evently.mapper.GuestMapper;
import com.evently.repository.EventRepository;
import com.evently.repository.GuestRepository;
import com.evently.repository.UserRepository;

/**
 * Service layer for Event-related operations.
 * Handles event CRUD operations, guest management, and event queries.
 */
@Service
@Transactional
public class EventService {
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private GuestRepository guestRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EventMapper eventMapper;
    
    @Autowired
    private GuestMapper guestMapper;
    
    // Event Management
    
    /**
     * Create event.
     */
    public EventDTO createEvent(EventCreateDTO createDTO, Long plannerId) {
        User planner = userRepository.findById(plannerId)
                .orElseThrow(() -> new IllegalArgumentException("Planner not found: " + plannerId));
        
        if (!Boolean.TRUE.equals(planner.getIsPlanner()) && !Boolean.TRUE.equals(planner.getIsVendor())) {
            throw new IllegalArgumentException("User is not authorized to create events: " + plannerId);
        }
        
        Event event = eventMapper.toEvent(createDTO);
        event.setPlanner(planner);
        
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventDTO(savedEvent);
    }
    
    /**
     * Get event by ID.
     */
    @Transactional(readOnly = true)
    public Optional<EventDTO> getEventById(Long id) {
        return eventRepository.findById(id)
                .map(eventMapper::toEventDTO);
    }
    
    /**
     * Update event.
     */
    public EventDTO updateEvent(Long eventId, EventUpdateDTO updateDTO, Long plannerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        
        // Check if the user owns this event
        if (!event.getPlanner().getId().equals(plannerId)) {
            throw new IllegalArgumentException("User is not authorized to update this event");
        }
        
        eventMapper.updateEventFromDTO(updateDTO, event);
        
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toEventDTO(savedEvent);
    }
    
    /**
     * Delete event.
     */
    public void deleteEvent(Long eventId, Long plannerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        
        // Check if the user owns this event
        if (!event.getPlanner().getId().equals(plannerId)) {
            throw new IllegalArgumentException("User is not authorized to delete this event");
        }
        
        eventRepository.delete(event);
    }
    
    // Query Methods
    
    /**
     * Get all events with pagination.
     */
    @Transactional(readOnly = true)
    public Page<EventDTO> getAllEvents(Pageable pageable) {
        Page<Event> events = eventRepository.findAll(pageable);
        return events.map(eventMapper::toEventDTO);
    }
    
    /**
     * Get events by planner.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByPlanner(Long plannerId) {
        List<Event> events = eventRepository.findByPlannerId(plannerId);
        return eventMapper.toEventDTOList(events);
    }
    
    /**
     * Get upcoming events.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> getUpcomingEvents() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventRepository.findUpcomingEvents(now);
        return eventMapper.toEventDTOList(events);
    }
    
    /**
     * Get events by location.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsByLocation(String location) {
        List<Event> events = eventRepository.findByLocationContainingIgnoreCase(location);
        return eventMapper.toEventDTOList(events);
    }
    
    /**
     * Search events by title or location.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> searchEvents(String query) {
        List<Event> events = eventRepository.findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(query, query);
        return eventMapper.toEventDTOList(events);
    }
    
    // Guest Management
    
    /**
     * Add guest to event.
     */
    public GuestDTO addGuestToEvent(Long eventId, GuestCreateUpdateDTO guestDTO, Long plannerId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        
        // Check if the user owns this event
        if (!event.getPlanner().getId().equals(plannerId)) {
            throw new IllegalArgumentException("User is not authorized to add guests to this event");
        }
        
        // Check if guest email already exists for this event
        if (guestRepository.existsByEventIdAndEmail(eventId, guestDTO.getEmail())) {
            throw new IllegalArgumentException("Guest with this email is already invited to this event");
        }
        
        Guest guest = guestMapper.toGuest(guestDTO);
        guest.setEvent(event);
        
        // Set user if email matches existing user
        userRepository.findByEmail(guestDTO.getEmail())
                .ifPresent(guest::setUser);
        
        Guest savedGuest = guestRepository.save(guest);
        return guestMapper.toGuestDTO(savedGuest);
    }
    
    /**
     * Update guest.
     */
    public GuestDTO updateGuest(Long guestId, GuestCreateUpdateDTO guestDTO, Long plannerId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found: " + guestId));
        
        // Check if the user owns the event
        if (!guest.getEvent().getPlanner().getId().equals(plannerId)) {
            throw new IllegalArgumentException("User is not authorized to update guests for this event");
        }
        
        guestMapper.updateGuestFromDTO(guestDTO, guest);
        
        Guest savedGuest = guestRepository.save(guest);
        return guestMapper.toGuestDTO(savedGuest);
    }
    
    /**
     * Remove guest from event.
     */
    public void removeGuestFromEvent(Long guestId, Long plannerId) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found: " + guestId));
        
        // Check if the user owns the event
        if (!guest.getEvent().getPlanner().getId().equals(plannerId)) {
            throw new IllegalArgumentException("User is not authorized to remove guests from this event");
        }
        
        guestRepository.delete(guest);
    }
    
    /**
     * Get guests by event.
     */
    @Transactional(readOnly = true)
    public List<GuestDTO> getGuestsByEvent(Long eventId) {
        List<Guest> guests = guestRepository.findByEventId(eventId);
        return guestMapper.toGuestDTOList(guests);
    }
    
    /**
     * Get guests by RSVP status.
     */
    @Transactional(readOnly = true)
    public List<GuestDTO> getGuestsByEventAndRsvpStatus(Long eventId, Guest.RsvpStatus rsvpStatus) {
        List<Guest> guests = guestRepository.findByEventIdAndRsvpStatus(eventId, rsvpStatus);
        return guestMapper.toGuestDTOList(guests);
    }
    
    /**
     * Update guest RSVP status.
     */
    public GuestDTO updateGuestRsvpStatus(Long guestId, Guest.RsvpStatus rsvpStatus) {
        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("Guest not found: " + guestId));
        
        guest.setRsvpStatus(rsvpStatus);
        
        Guest savedGuest = guestRepository.save(guest);
        return guestMapper.toGuestDTO(savedGuest);
    }
    
    // Statistics Methods
    
    /**
     * Get event statistics (guest counts by RSVP status).
     */
    @Transactional(readOnly = true)
    public EventStatsDTO getEventStatistics(Long eventId) {
        // Verify event exists
        eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        
        int totalGuests = guestRepository.countByEventId(eventId);
        int attendingGuests = guestRepository.countByEventIdAndRsvpStatus(eventId, Guest.RsvpStatus.ATTENDING);
        int declinedGuests = guestRepository.countByEventIdAndRsvpStatus(eventId, Guest.RsvpStatus.DECLINED);
        int pendingGuests = guestRepository.countByEventIdAndRsvpStatus(eventId, Guest.RsvpStatus.INVITED);
        
        return new EventStatsDTO(totalGuests, attendingGuests, declinedGuests, pendingGuests);
    }
    
    /**
     * DTO for event statistics.
     */
    public static class EventStatsDTO {
        private final int totalGuests;
        private final int attendingGuests;
        private final int declinedGuests;
        private final int pendingGuests;
        
        public EventStatsDTO(int totalGuests, int attendingGuests, int declinedGuests, int pendingGuests) {
            this.totalGuests = totalGuests;
            this.attendingGuests = attendingGuests;
            this.declinedGuests = declinedGuests;
            this.pendingGuests = pendingGuests;
        }
        
        public int getTotalGuests() { return totalGuests; }
        public int getAttendingGuests() { return attendingGuests; }
        public int getDeclinedGuests() { return declinedGuests; }
        public int getPendingGuests() { return pendingGuests; }
    }
}