package com.evently.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evently.model.Event;
import com.evently.model.Service;
import com.evently.model.VendorBooking;
import com.evently.model.VendorBooking.BookingStatus;
import com.evently.model.VendorProfile;

/**
 * Repository interface for VendorBooking entity operations.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface VendorBookingRepository extends JpaRepository<VendorBooking, Long> {
    
    // Find bookings by event
    List<VendorBooking> findByEvent(Event event);
    
    List<VendorBooking> findByEventId(Long eventId);
    
    // Find bookings by vendor
    List<VendorBooking> findByVendor(VendorProfile vendor);
    
    List<VendorBooking> findByVendorId(Long vendorId);
    
    // Find bookings by service
    List<VendorBooking> findByService(Service service);
    
    List<VendorBooking> findByServiceId(Long serviceId);
    
    // Find bookings by status
    List<VendorBooking> findByStatus(BookingStatus status);
    
    // Find bookings by event and status
    List<VendorBooking> findByEventAndStatus(Event event, BookingStatus status);
    
    // Find bookings by vendor and status
    List<VendorBooking> findByVendorAndStatus(VendorProfile vendor, BookingStatus status);
    
    List<VendorBooking> findByVendorIdAndStatus(Long vendorId, BookingStatus status);
    
    // Find booking by event and vendor
    Optional<VendorBooking> findByEventAndVendor(Event event, VendorProfile vendor);
    
    Optional<VendorBooking> findByEventAndVendorId(Event event, Long vendorId);
    
    // Find booking by event and service
    Optional<VendorBooking> findByEventAndService(Event event, Service service);
    
    Optional<VendorBooking> findByEventAndServiceId(Event event, Long serviceId);
    
    // Custom queries
    @Query("SELECT vb FROM VendorBooking vb WHERE vb.event = :event AND vb.status IN :statuses")
    List<VendorBooking> findByEventAndStatusIn(@Param("event") Event event, @Param("statuses") List<BookingStatus> statuses);
    
    @Query("SELECT vb FROM VendorBooking vb WHERE vb.vendor = :vendor AND vb.status IN :statuses")
    List<VendorBooking> findByVendorAndStatusIn(@Param("vendor") VendorProfile vendor, @Param("statuses") List<BookingStatus> statuses);
    
    @Query("SELECT vb FROM VendorBooking vb WHERE vb.vendor.id = :vendorId AND vb.status IN :statuses")
    List<VendorBooking> findByVendorIdAndStatusIn(@Param("vendorId") Long vendorId, @Param("statuses") List<BookingStatus> statuses);
    
    @Query("SELECT COUNT(vb) FROM VendorBooking vb WHERE vb.event = :event AND vb.status = :status")
    long countByEventAndStatus(@Param("event") Event event, @Param("status") BookingStatus status);
    
    @Query("SELECT COUNT(vb) FROM VendorBooking vb WHERE vb.vendor = :vendor AND vb.status = :status")
    long countByVendorAndStatus(@Param("vendor") VendorProfile vendor, @Param("status") BookingStatus status);
    
    @Query("SELECT COUNT(vb) FROM VendorBooking vb WHERE vb.vendor.id = :vendorId AND vb.status = :status")
    long countByVendorIdAndStatus(@Param("vendorId") Long vendorId, @Param("status") BookingStatus status);
    
    // Find upcoming bookings for vendor
    @Query("SELECT vb FROM VendorBooking vb WHERE vb.vendor = :vendor AND vb.event.date >= CURRENT_TIMESTAMP AND vb.status = 'CONFIRMED' ORDER BY vb.event.date ASC")
    List<VendorBooking> findUpcomingConfirmedBookingsByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT vb FROM VendorBooking vb WHERE vb.vendor.id = :vendorId AND vb.event.date >= CURRENT_TIMESTAMP AND vb.status = 'CONFIRMED' ORDER BY vb.event.date ASC")
    List<VendorBooking> findUpcomingConfirmedBookingsByVendorId(@Param("vendorId") Long vendorId);
    
    // Statistics queries
    @Query("SELECT vb.status, COUNT(vb) FROM VendorBooking vb WHERE vb.event = :event GROUP BY vb.status")
    List<Object[]> getBookingStatsByEvent(@Param("event") Event event);
    
    @Query("SELECT vb.status, COUNT(vb) FROM VendorBooking vb WHERE vb.vendor = :vendor GROUP BY vb.status")
    List<Object[]> getBookingStatsByVendor(@Param("vendor") VendorProfile vendor);
    
    @Query("SELECT vb.status, COUNT(vb) FROM VendorBooking vb WHERE vb.vendor.id = :vendorId GROUP BY vb.status")
    List<Object[]> getBookingStatsByVendorId(@Param("vendorId") Long vendorId);
    
    // Check if vendor is already booked for event
    @Query("SELECT CASE WHEN COUNT(vb) > 0 THEN true ELSE false END FROM VendorBooking vb WHERE vb.event = :event AND vb.vendor = :vendor")
    boolean isVendorBookedForEvent(@Param("event") Event event, @Param("vendor") VendorProfile vendor);
    
    @Query("SELECT CASE WHEN COUNT(vb) > 0 THEN true ELSE false END FROM VendorBooking vb WHERE vb.event = :event AND vb.vendor.id = :vendorId")
    boolean isVendorBookedForEventById(@Param("event") Event event, @Param("vendorId") Long vendorId);
    
    // Find all bookings for events managed by a specific planner
    @Query("SELECT vb FROM VendorBooking vb WHERE vb.event.planner.id = :plannerId")
    List<VendorBooking> findBookingsByPlannerId(@Param("plannerId") Long plannerId);
    
    // Additional methods needed by BookingService
    boolean existsByEventIdAndVendorId(Long eventId, Long vendorId);
    
    List<VendorBooking> findByEventPlannerId(Long plannerId);
    
    List<VendorBooking> findByEventPlannerIdAndStatus(Long plannerId, BookingStatus status);
    
    List<VendorBooking> findByEventDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<VendorBooking> findByVendorIdAndEventDateBetween(Long vendorId, LocalDate startDate, LocalDate endDate);
    
    boolean existsByVendorIdAndEventDateAndStatusIn(Long vendorId, LocalDate date, List<BookingStatus> statuses);
    
    @Query("SELECT e.date FROM VendorBooking vb JOIN vb.event e WHERE vb.vendor.id = :vendorId AND e.date BETWEEN :startDate AND :endDate AND vb.status IN (:statuses)")
    List<LocalDate> findUnavailableDatesByVendorAndDateRange(@Param("vendorId") Long vendorId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("statuses") List<BookingStatus> statuses);
    
    int countByVendorId(Long vendorId);
    
    int countByEventPlannerId(Long plannerId);
    
    int countByEventPlannerIdAndStatus(Long plannerId, BookingStatus status);
    
    List<VendorBooking> findByVendorIdAndEventDateGreaterThanEqualAndStatusIn(Long vendorId, LocalDate date, List<BookingStatus> statuses);
    
    List<VendorBooking> findByEventPlannerIdAndEventDateGreaterThanEqualAndStatusIn(Long plannerId, LocalDate date, List<BookingStatus> statuses);
}