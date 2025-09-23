package com.evently.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import com.evently.dto.booking.VendorBookingCreateDTO;
import com.evently.dto.booking.VendorBookingDTO;
import com.evently.model.VendorBooking.BookingStatus;
import com.evently.service.BookingService;
import com.evently.service.UserService;
import com.evently.service.BookingService.BookingStatsDTO;

import jakarta.validation.Valid;

/**
 * REST Controller for Booking management.
 * Provides endpoints for vendor-event booking relationships and availability management.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    
    @Autowired
    private BookingService bookingService;
    
    @Autowired
    private UserService userService;
    
    // Booking Management
    
    /**
     * Create new booking.
     */
    @PostMapping
    public ResponseEntity<VendorBookingDTO> createBooking(
            @Valid @RequestBody VendorBookingCreateDTO createDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID (planner)
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            VendorBookingDTO booking = bookingService.createVendorBooking(createDTO, plannerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(booking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get booking by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VendorBookingDTO> getBookingById(@PathVariable Long id) {
        return bookingService.getVendorBookingById(id)
                .map(booking -> ResponseEntity.ok(booking))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update booking.
     */
    @PutMapping("/{id}")
    public ResponseEntity<VendorBookingDTO> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody VendorBookingCreateDTO updateDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            VendorBookingDTO updatedBooking = bookingService.updateVendorBooking(id, updateDTO, userId);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Cancel booking.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            bookingService.cancelVendorBooking(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Booking Status Management
    
    /**
     * Update booking status.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<VendorBookingDTO> updateBookingStatus(
            @PathVariable Long id,
            @RequestParam BookingStatus status,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            VendorBookingDTO updatedBooking = bookingService.updateBookingStatus(id, status, userId);
            return ResponseEntity.ok(updatedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Confirm booking (vendor only).
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<VendorBookingDTO> confirmBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long vendorUserId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            VendorBookingDTO confirmedBooking = bookingService.confirmVendorBooking(id, vendorUserId);
            return ResponseEntity.ok(confirmedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Reject booking (vendor only).
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<VendorBookingDTO> rejectBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long vendorUserId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            VendorBookingDTO rejectedBooking = bookingService.cancelVendorBooking(id, vendorUserId);
            return ResponseEntity.ok(rejectedBooking);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Query Endpoints
    
    /**
     * Get all bookings with pagination.
     */
    @GetMapping
    public ResponseEntity<Page<VendorBookingDTO>> getAllBookings(Pageable pageable) {
        Page<VendorBookingDTO> bookings = bookingService.getAllVendorBookings(pageable);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by event.
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<VendorBookingDTO>> getBookingsByEvent(@PathVariable Long eventId) {
        List<VendorBookingDTO> bookings = bookingService.getBookingsByEvent(eventId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by vendor.
     */
    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<VendorBookingDTO>> getBookingsByVendor(@PathVariable Long vendorId) {
        List<VendorBookingDTO> bookings = bookingService.getBookingsByVendor(vendorId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get bookings by planner.
     */
    @GetMapping("/planner/{plannerId}")
    public ResponseEntity<List<VendorBookingDTO>> getBookingsByPlanner(@PathVariable Long plannerId) {
        List<VendorBookingDTO> bookings = bookingService.getBookingsByCustomer(plannerId);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get current user's bookings (as planner).
     */
    @GetMapping("/my-bookings")
    public ResponseEntity<List<VendorBookingDTO>> getMyBookings(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID
            Long plannerId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            List<VendorBookingDTO> bookings = bookingService.getBookingsByCustomer(plannerId);
            return ResponseEntity.ok(bookings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get current vendor's bookings (as vendor).
     */
    @GetMapping("/my-vendor-bookings")
    public ResponseEntity<List<VendorBookingDTO>> getMyVendorBookings(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Get the current user's ID and then vendor profile
            Long userId = userService.getUserByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"))
                    .getId();
            
            // For now, assume vendor user ID maps to vendor profile ID
            List<VendorBookingDTO> bookings = bookingService.getBookingsByVendor(userId);
            return ResponseEntity.ok(bookings);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get bookings by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<VendorBookingDTO>> getBookingsByStatus(@PathVariable BookingStatus status) {
        List<VendorBookingDTO> bookings = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get confirmed bookings.
     */
    @GetMapping("/confirmed")
    public ResponseEntity<List<VendorBookingDTO>> getConfirmedBookings() {
        List<VendorBookingDTO> bookings = bookingService.getBookingsByStatus(BookingStatus.CONFIRMED);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get pending bookings.
     */
    @GetMapping("/pending")
    public ResponseEntity<List<VendorBookingDTO>> getPendingBookings() {
        List<VendorBookingDTO> bookings = bookingService.getBookingsByStatus(BookingStatus.PENDING);
        return ResponseEntity.ok(bookings);
    }
    
    // Date Range Queries
    
    /**
     * Get bookings in date range.
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<VendorBookingDTO>> getBookingsInDateRange(
            @RequestParam LocalDateTime startDateTime,
            @RequestParam LocalDateTime endDateTime) {
        // Convert to LocalDate for service call
        List<VendorBookingDTO> bookings = bookingService.getBookingsByDateRange(
            startDateTime.toLocalDate(), endDateTime.toLocalDate());
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get upcoming bookings.
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<VendorBookingDTO>> getUpcomingBookings() {
        // Get all confirmed and upcoming bookings
        List<VendorBookingDTO> bookings = bookingService.getBookingsByStatus(BookingStatus.CONFIRMED);
        return ResponseEntity.ok(bookings);
    }
    
    /**
     * Get recent bookings.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<VendorBookingDTO>> getRecentBookings() {
        // Get all completed bookings
        List<VendorBookingDTO> bookings = bookingService.getBookingsByStatus(BookingStatus.COMPLETED);
        return ResponseEntity.ok(bookings);
    }
    
    // Availability Management
    
    /**
     * Check vendor availability for date.
     */
    @GetMapping("/availability/vendor/{vendorId}")
    public ResponseEntity<Boolean> checkVendorAvailability(
            @PathVariable Long vendorId,
            @RequestParam LocalDate eventDate) {
        try {
            boolean isAvailable = bookingService.isVendorAvailable(vendorId, eventDate);
            return ResponseEntity.ok(isAvailable);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Statistics Endpoints
    
    /**
     * Get booking statistics by vendor.
     */
    @GetMapping("/stats/vendor/{vendorId}")
    public ResponseEntity<BookingStatsDTO> getBookingStatisticsByVendor(@PathVariable Long vendorId) {
        try {
            BookingStatsDTO stats = bookingService.getVendorBookingStatistics(vendorId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get booking statistics by planner.
     */
    @GetMapping("/stats/planner/{plannerId}")
    public ResponseEntity<BookingStatsDTO> getBookingStatisticsByPlanner(@PathVariable Long plannerId) {
        try {
            BookingStatsDTO stats = bookingService.getCustomerBookingStatistics(plannerId);
            return ResponseEntity.ok(stats);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}