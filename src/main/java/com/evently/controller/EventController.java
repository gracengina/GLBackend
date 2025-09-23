package com.evently.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.evently.model.Guest;
import com.evently.dto.booking.GuestCreateUpdateDTO;
import com.evently.dto.booking.GuestDTO;
import com.evently.dto.event.EventCreateDTO;
import com.evently.dto.event.EventDTO;
import com.evently.dto.event.EventUpdateDTO;
import com.evently.service.EventService;
import com.evently.service.EventService.EventStatsDTO;
import com.evently.service.UserService;

import jakarta.validation.Valid;

/**
 * REST Controller for Event management.
 * Provides endpoints for event CRUD operations, guest management, and event queries.
 */
@RestController
@RequestMapping("/api/events")
public class EventController {
    
    @Autowired
    private EventService eventService;
    
    @Autowired
    private UserService userService;
    
    // Event Management
    
    /**
     * Create a new event.
     */
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @Valid @RequestBody EventCreateDTO createDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            EventDTO event = eventService.createEvent(createDTO, plannerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(event);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get event by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(event -> ResponseEntity.ok(event))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update event.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody EventUpdateDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            EventDTO updatedEvent = eventService.updateEvent(id, updateDTO, plannerId);
            return ResponseEntity.ok(updatedEvent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Delete event.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            eventService.deleteEvent(id, plannerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Query Endpoints
    
    /**
     * Get all events for the current user.
     */
    @GetMapping("/my-events")
    public ResponseEntity<List<EventDTO>> getMyEvents(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            List<EventDTO> events = eventService.getEventsByPlanner(plannerId);
            return ResponseEntity.ok(events);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get events by planner ID.
     */
    @GetMapping("/planner/{plannerId}")
    public ResponseEntity<List<EventDTO>> getEventsByPlanner(@PathVariable Long plannerId) {
        List<EventDTO> events = eventService.getEventsByPlanner(plannerId);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get upcoming events.
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<EventDTO>> getUpcomingEvents() {
        List<EventDTO> events = eventService.getUpcomingEvents();
        return ResponseEntity.ok(events);
    }
    
    /**
     * Search events by title or location.
     */
    @GetMapping("/search")
    public ResponseEntity<List<EventDTO>> searchEvents(@RequestParam String query) {
        List<EventDTO> events = eventService.searchEvents(query);
        return ResponseEntity.ok(events);
    }
    
    /**
     * Get events by location.
     */
    @GetMapping("/location")
    public ResponseEntity<List<EventDTO>> getEventsByLocation(@RequestParam String location) {
        List<EventDTO> events = eventService.getEventsByLocation(location);
        return ResponseEntity.ok(events);
    }
    
    // Guest Management
    
    /**
     * Add guest to event.
     */
    @PostMapping("/{eventId}/guests")
    public ResponseEntity<GuestDTO> addGuestToEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody GuestCreateUpdateDTO guestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            GuestDTO guest = eventService.addGuestToEvent(eventId, guestDTO, plannerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(guest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get guests for an event.
     */
    @GetMapping("/{eventId}/guests")
    public ResponseEntity<List<GuestDTO>> getGuestsByEvent(@PathVariable Long eventId) {
        List<GuestDTO> guests = eventService.getGuestsByEvent(eventId);
        return ResponseEntity.ok(guests);
    }
    
    /**
     * Get guests by RSVP status.
     */
    @GetMapping("/{eventId}/guests/status/{status}")
    public ResponseEntity<List<GuestDTO>> getGuestsByEventAndRsvpStatus(
            @PathVariable Long eventId,
            @PathVariable String status) {
        try {
            Guest.RsvpStatus rsvpStatus = Guest.RsvpStatus.valueOf(status.toUpperCase());
            List<GuestDTO> guests = eventService.getGuestsByEventAndRsvpStatus(eventId, rsvpStatus);
            return ResponseEntity.ok(guests);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update guest.
     */
    @PutMapping("/guests/{guestId}")
    public ResponseEntity<GuestDTO> updateGuest(
            @PathVariable Long guestId,
            @Valid @RequestBody GuestCreateUpdateDTO guestDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            GuestDTO updatedGuest = eventService.updateGuest(guestId, guestDTO, plannerId);
            return ResponseEntity.ok(updatedGuest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Remove guest from event.
     */
    @DeleteMapping("/guests/{guestId}")
    public ResponseEntity<Void> removeGuestFromEvent(
            @PathVariable Long guestId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            eventService.removeGuestFromEvent(guestId, plannerId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Update guest RSVP status.
     */
    @PutMapping("/guests/{guestId}/rsvp/{status}")
    public ResponseEntity<GuestDTO> updateGuestRsvpStatus(
            @PathVariable Long guestId,
            @PathVariable String status) {
        try {
            Guest.RsvpStatus rsvpStatus = Guest.RsvpStatus.valueOf(status.toUpperCase());
            GuestDTO updatedGuest = eventService.updateGuestRsvpStatus(guestId, rsvpStatus);
            return ResponseEntity.ok(updatedGuest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Statistics Endpoints
    
    /**
     * Get event statistics.
     */
    @GetMapping("/{eventId}/stats")
    public ResponseEntity<EventStatsDTO> getEventStatistics(@PathVariable Long eventId) {
        try {
            EventStatsDTO stats = eventService.getEventStatistics(eventId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}