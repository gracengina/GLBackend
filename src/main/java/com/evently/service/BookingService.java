package com.evently.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evently.model.Event;
import com.evently.model.VendorBooking;
import com.evently.model.VendorBooking.BookingStatus;
import com.evently.model.VendorProfile;
import com.evently.dto.booking.VendorBookingCreateDTO;
import com.evently.dto.booking.VendorBookingDTO;
import com.evently.mapper.VendorBookingMapper;
import com.evently.repository.EventRepository;
import com.evently.repository.VendorBookingRepository;
import com.evently.repository.VendorProfileRepository;

/**
 * Service layer for Booking-related operations.
 * Handles vendor-event relationships and booking management.
 */
@Service
@Transactional
public class BookingService {
    
    @Autowired
    private VendorBookingRepository vendorBookingRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private VendorProfileRepository vendorProfileRepository;
    
    @Autowired
    private VendorBookingMapper vendorBookingMapper;
    
    // Booking Management
    
    /**
     * Create vendor booking.
     */
    public VendorBookingDTO createVendorBooking(VendorBookingCreateDTO createDTO, Long customerUserId) {
        Event event = eventRepository.findById(createDTO.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + createDTO.getEventId()));
        
        VendorProfile vendor = vendorProfileRepository.findById(createDTO.getVendorId())
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found: " + createDTO.getVendorId()));
        
        // Verify the customer owns the event
        if (!event.getPlanner().getId().equals(customerUserId)) {
            throw new IllegalArgumentException("User is not authorized to create bookings for this event");
        }
        
        // Check if vendor is already booked for this event
        if (vendorBookingRepository.existsByEventIdAndVendorId(createDTO.getEventId(), createDTO.getVendorId())) {
            throw new IllegalArgumentException("Vendor is already booked for this event");
        }
        
        // Check vendor availability for the event date
        if (isVendorBookedOnDate(createDTO.getVendorId(), event.getDate().toLocalDate())) {
            throw new IllegalArgumentException("Vendor is not available on the event date");
        }
        
        VendorBooking vendorBooking = vendorBookingMapper.toVendorBooking(createDTO);
        vendorBooking.setEvent(event);
        vendorBooking.setVendor(vendor);
        vendorBooking.setStatus(BookingStatus.PENDING);
        
        VendorBooking savedBooking = vendorBookingRepository.save(vendorBooking);
        return vendorBookingMapper.toVendorBookingDTO(savedBooking);
    }
    
    /**
     * Get vendor booking by ID.
     */
    @Transactional(readOnly = true)
    public Optional<VendorBookingDTO> getVendorBookingById(Long id) {
        return vendorBookingRepository.findById(id)
                .map(vendorBookingMapper::toVendorBookingDTO);
    }
    
    /**
     * Update vendor booking.
     */
    public VendorBookingDTO updateVendorBooking(Long bookingId, VendorBookingCreateDTO updateDTO, Long userId) {
        VendorBooking vendorBooking = vendorBookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor booking not found: " + bookingId));
        
        // Check authorization - customer or vendor can update
        boolean isCustomer = vendorBooking.getEvent().getPlanner().getId().equals(userId);
        boolean isVendor = vendorBooking.getVendor().getUser().getId().equals(userId);
        
        if (!isCustomer && !isVendor) {
            throw new IllegalArgumentException("User is not authorized to update this booking");
        }
        
        // Update fields that can be changed
        if (updateDTO.getNotes() != null) {
            vendorBooking.setNotes(updateDTO.getNotes());
        }
        
        VendorBooking savedBooking = vendorBookingRepository.save(vendorBooking);
        return vendorBookingMapper.toVendorBookingDTO(savedBooking);
    }
    
    /**
     * Update booking status.
     */
    public VendorBookingDTO updateBookingStatus(Long bookingId, BookingStatus status, Long userId) {
        VendorBooking vendorBooking = vendorBookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor booking not found: " + bookingId));
        
        // Check authorization - customer or vendor can update status
        boolean isCustomer = vendorBooking.getEvent().getPlanner().getId().equals(userId);
        boolean isVendor = vendorBooking.getVendor().getUser().getId().equals(userId);
        
        if (!isCustomer && !isVendor) {
            throw new IllegalArgumentException("User is not authorized to update this booking status");
        }
        
        // Business logic for status transitions
        if (status == BookingStatus.CONFIRMED && vendorBooking.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Can only confirm pending bookings");
        }
        
        if (status == BookingStatus.CANCELLED && vendorBooking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot cancel completed bookings");
        }
        
        vendorBooking.setStatus(status);
        
        VendorBooking savedBooking = vendorBookingRepository.save(vendorBooking);
        return vendorBookingMapper.toVendorBookingDTO(savedBooking);
    }
    
    /**
     * Cancel vendor booking.
     */
    public VendorBookingDTO cancelVendorBooking(Long bookingId, Long userId) {
        return updateBookingStatus(bookingId, BookingStatus.CANCELLED, userId);
    }
    
    /**
     * Confirm vendor booking.
     */
    public VendorBookingDTO confirmVendorBooking(Long bookingId, Long userId) {
        return updateBookingStatus(bookingId, BookingStatus.CONFIRMED, userId);
    }
    
    /**
     * Complete vendor booking.
     */
    public VendorBookingDTO completeVendorBooking(Long bookingId, Long userId) {
        return updateBookingStatus(bookingId, BookingStatus.COMPLETED, userId);
    }
    
    /**
     * Delete vendor booking.
     */
    public void deleteVendorBooking(Long bookingId, Long userId) {
        VendorBooking vendorBooking = vendorBookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor booking not found: " + bookingId));
        
        // Check authorization - only customer can delete
        if (!vendorBooking.getEvent().getPlanner().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this booking");
        }
        
        // Can only delete pending or cancelled bookings
        if (vendorBooking.getStatus() == BookingStatus.CONFIRMED || vendorBooking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot delete confirmed or completed bookings");
        }
        
        vendorBookingRepository.delete(vendorBooking);
    }
    
    // Query Methods
    
    /**
     * Get all vendor bookings with pagination.
     */
    @Transactional(readOnly = true)
    public Page<VendorBookingDTO> getAllVendorBookings(Pageable pageable) {
        Page<VendorBooking> bookings = vendorBookingRepository.findAll(pageable);
        return bookings.map(vendorBookingMapper::toVendorBookingDTO);
    }
    
    /**
     * Get bookings by event.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getBookingsByEvent(Long eventId) {
        List<VendorBooking> bookings = vendorBookingRepository.findByEventId(eventId);
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * Get bookings by vendor.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getBookingsByVendor(Long vendorId) {
        List<VendorBooking> bookings = vendorBookingRepository.findByVendorId(vendorId);
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * Get bookings by customer (event planner).
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getBookingsByCustomer(Long customerId) {
        List<VendorBooking> bookings = vendorBookingRepository.findByEventPlannerId(customerId);
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * Get bookings by status.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getBookingsByStatus(BookingStatus status) {
        List<VendorBooking> bookings = vendorBookingRepository.findByStatus(status);
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * Get vendor bookings by status.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getVendorBookingsByStatus(Long vendorId, BookingStatus status) {
        List<VendorBooking> bookings = vendorBookingRepository.findByVendorIdAndStatus(vendorId, status);
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * Get customer bookings by status.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getCustomerBookingsByStatus(Long customerId, BookingStatus status) {
        List<VendorBooking> bookings = vendorBookingRepository.findByEventPlannerIdAndStatus(customerId, status);
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * Get bookings by date range.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<VendorBooking> bookings = vendorBookingRepository.findByEventDateBetween(startDate, endDate);
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * Get vendor bookings by date range.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getVendorBookingsByDateRange(Long vendorId, LocalDate startDate, LocalDate endDate) {
        List<VendorBooking> bookings = vendorBookingRepository.findByVendorIdAndEventDateBetween(vendorId, startDate, endDate);
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    // Availability Methods
    
    /**
     * Check if vendor is available on a specific date.
     */
    @Transactional(readOnly = true)
    public boolean isVendorAvailable(Long vendorId, LocalDate date) {
        return !isVendorBookedOnDate(vendorId, date);
    }
    
    /**
     * Check if vendor is booked on a specific date.
     */
    @Transactional(readOnly = true)
    public boolean isVendorBookedOnDate(Long vendorId, LocalDate date) {
        return vendorBookingRepository.existsByVendorIdAndEventDateAndStatusIn(
                vendorId, date, List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED));
    }
    
    /**
     * Get vendor availability for a date range.
     */
    @Transactional(readOnly = true)
    public List<LocalDate> getVendorUnavailableDates(Long vendorId, LocalDate startDate, LocalDate endDate) {
        return vendorBookingRepository.findUnavailableDatesByVendorAndDateRange(vendorId, startDate, endDate, 
                List.of(BookingStatus.CONFIRMED, BookingStatus.COMPLETED));
    }
    
    // Statistics Methods
    
    /**
     * Get booking statistics for a vendor.
     */
    @Transactional(readOnly = true)
    public BookingStatsDTO getVendorBookingStatistics(Long vendorId) {
        int totalBookings = (int) vendorBookingRepository.countByVendorId(vendorId);
        int pendingBookings = (int) vendorBookingRepository.countByVendorIdAndStatus(vendorId, BookingStatus.PENDING);
        int confirmedBookings = (int) vendorBookingRepository.countByVendorIdAndStatus(vendorId, BookingStatus.CONFIRMED);
        int completedBookings = (int) vendorBookingRepository.countByVendorIdAndStatus(vendorId, BookingStatus.COMPLETED);
        int cancelledBookings = (int) vendorBookingRepository.countByVendorIdAndStatus(vendorId, BookingStatus.CANCELLED);
        
        return new BookingStatsDTO(totalBookings, pendingBookings, confirmedBookings, completedBookings, cancelledBookings);
    }
    
    /**
     * Get booking statistics for a customer.
     */
    @Transactional(readOnly = true)
    public BookingStatsDTO getCustomerBookingStatistics(Long customerId) {
        int totalBookings = (int) vendorBookingRepository.countByEventPlannerId(customerId);
        int pendingBookings = (int) vendorBookingRepository.countByEventPlannerIdAndStatus(customerId, BookingStatus.PENDING);
        int confirmedBookings = (int) vendorBookingRepository.countByEventPlannerIdAndStatus(customerId, BookingStatus.CONFIRMED);
        int completedBookings = (int) vendorBookingRepository.countByEventPlannerIdAndStatus(customerId, BookingStatus.COMPLETED);
        int cancelledBookings = (int) vendorBookingRepository.countByEventPlannerIdAndStatus(customerId, BookingStatus.CANCELLED);
        
        return new BookingStatsDTO(totalBookings, pendingBookings, confirmedBookings, completedBookings, cancelledBookings);
    }
    
    /**
     * Get upcoming bookings for vendor.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getUpcomingVendorBookings(Long vendorId) {
        LocalDate today = LocalDate.now();
        List<VendorBooking> bookings = vendorBookingRepository.findByVendorIdAndEventDateGreaterThanEqualAndStatusIn(
                vendorId, today, List.of(BookingStatus.CONFIRMED));
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * Get upcoming bookings for customer.
     */
    @Transactional(readOnly = true)
    public List<VendorBookingDTO> getUpcomingCustomerBookings(Long customerId) {
        LocalDate today = LocalDate.now();
        List<VendorBooking> bookings = vendorBookingRepository.findByEventPlannerIdAndEventDateGreaterThanEqualAndStatusIn(
                customerId, today, List.of(BookingStatus.CONFIRMED));
        return vendorBookingMapper.toVendorBookingDTOList(bookings);
    }
    
    /**
     * DTO for booking statistics.
     */
    public static class BookingStatsDTO {
        private final int totalBookings;
        private final int pendingBookings;
        private final int confirmedBookings;
        private final int completedBookings;
        private final int cancelledBookings;
        
        public BookingStatsDTO(int totalBookings, int pendingBookings, int confirmedBookings, 
                              int completedBookings, int cancelledBookings) {
            this.totalBookings = totalBookings;
            this.pendingBookings = pendingBookings;
            this.confirmedBookings = confirmedBookings;
            this.completedBookings = completedBookings;
            this.cancelledBookings = cancelledBookings;
        }
        
        public int getTotalBookings() { return totalBookings; }
        public int getPendingBookings() { return pendingBookings; }
        public int getConfirmedBookings() { return confirmedBookings; }
        public int getCompletedBookings() { return completedBookings; }
        public int getCancelledBookings() { return cancelledBookings; }
    }
}