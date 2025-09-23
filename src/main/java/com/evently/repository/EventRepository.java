package com.evently.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.Event;
import com.evently.model.User;

/**
 * Repository interface for Event entity operations.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    // Find events by planner
    List<Event> findByPlanner(User planner);
    
    List<Event> findByPlannerId(Long plannerId);
    
    // Find events by date range
    List<Event> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find upcoming events
    List<Event> findByDateAfterOrderByDateAsc(LocalDateTime date);
    
    // Find past events
    List<Event> findByDateBeforeOrderByDateDesc(LocalDateTime date);
    
    // Find events by location (case insensitive)
    List<Event> findByLocationContainingIgnoreCase(String location);
    
    // Find events by title (case insensitive)
    List<Event> findByTitleContainingIgnoreCase(String title);
    
    // Custom queries
    @Query("SELECT e FROM Event e WHERE e.planner = :planner AND e.date >= :fromDate ORDER BY e.date ASC")
    List<Event> findUpcomingEventsByPlanner(@Param("planner") User planner, @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT e FROM Event e WHERE e.date >= :fromDate AND e.date <= :toDate ORDER BY e.date ASC")
    List<Event> findEventsInDateRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);
    
    @Query("SELECT COUNT(e) FROM Event e WHERE e.planner = :planner")
    long countEventsByPlanner(@Param("planner") User planner);
    
    // Find events with guest count
    @Query("SELECT e FROM Event e LEFT JOIN e.guests g GROUP BY e HAVING COUNT(g) >= :minGuests ORDER BY e.date ASC")
    List<Event> findEventsWithMinGuests(@Param("minGuests") long minGuests);
    
    // Find events with vendor bookings
    @Query("SELECT DISTINCT e FROM Event e JOIN e.vendorBookings vb WHERE vb.status = 'CONFIRMED'")
    List<Event> findEventsWithConfirmedVendors();
    
    // Additional methods needed by EventService
    @Query("SELECT e FROM Event e WHERE e.date >= :fromDate ORDER BY e.date ASC")
    List<Event> findUpcomingEvents(@Param("fromDate") LocalDateTime fromDate);
    
    // Search events by title or location
    List<Event> findByTitleContainingIgnoreCaseOrLocationContainingIgnoreCase(String title, String location);
}