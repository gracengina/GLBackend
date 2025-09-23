package com.evently.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.Event;
import com.evently.model.Guest;
import com.evently.model.Guest.RsvpStatus;
import com.evently.model.User;

/**
 * Repository interface for Guest entity operations.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {
    
    // Find guests by event
    List<Guest> findByEvent(Event event);
    
    List<Guest> findByEventId(Long eventId);
    
    // Find guests by RSVP status
    List<Guest> findByRsvpStatus(RsvpStatus rsvpStatus);
    
    List<Guest> findByEventAndRsvpStatus(Event event, RsvpStatus rsvpStatus);
    
    // Find guests by user
    List<Guest> findByUser(User user);
    
    List<Guest> findByUserId(Long userId);
    
    // Find guest by event and email
    Optional<Guest> findByEventAndEmail(Event event, String email);
    
    // Find guest by event and user
    Optional<Guest> findByEventAndUser(Event event, User user);
    
    // Find guests by email across all events
    List<Guest> findByEmail(String email);
    
    // Custom queries
    @Query("SELECT g FROM Guest g WHERE g.event = :event AND g.rsvpStatus IN :statuses")
    List<Guest> findByEventAndRsvpStatusIn(@Param("event") Event event, @Param("statuses") List<RsvpStatus> statuses);
    
    @Query("SELECT COUNT(g) FROM Guest g WHERE g.event = :event AND g.rsvpStatus = :status")
    long countByEventAndRsvpStatus(@Param("event") Event event, @Param("status") RsvpStatus status);
    
    @Query("SELECT g FROM Guest g WHERE g.user = :user AND g.event.date >= CURRENT_TIMESTAMP ORDER BY g.event.date ASC")
    List<Guest> findUpcomingEventsByUser(@Param("user") User user);
    
    // Statistics queries
    @Query("SELECT g.rsvpStatus, COUNT(g) FROM Guest g WHERE g.event = :event GROUP BY g.rsvpStatus")
    List<Object[]> getRsvpStatsByEvent(@Param("event") Event event);
    
    @Query("SELECT COUNT(g) FROM Guest g WHERE g.event = :event")
    long countGuestsByEvent(@Param("event") Event event);
    
    // Check if user is invited to event
    @Query("SELECT CASE WHEN COUNT(g) > 0 THEN true ELSE false END FROM Guest g WHERE g.event = :event AND (g.user = :user OR g.email = :email)")
    boolean isUserInvitedToEvent(@Param("event") Event event, @Param("user") User user, @Param("email") String email);
    
    // Additional methods needed by EventService
    List<Guest> findByEventIdAndRsvpStatus(Long eventId, RsvpStatus rsvpStatus);
    
    int countByEventId(Long eventId);
    
    int countByEventIdAndRsvpStatus(Long eventId, RsvpStatus rsvpStatus);
    
    boolean existsByEventIdAndEmail(Long eventId, String email);
}