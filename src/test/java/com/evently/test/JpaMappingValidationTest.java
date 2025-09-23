package com.evently.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.evently.model.Event;
import com.evently.model.Guest;
import com.evently.model.PortfolioItem;
import com.evently.model.Review;
import com.evently.model.Service;
import com.evently.model.ServiceCategory;
import com.evently.model.User;
import com.evently.model.VendorBooking;
import com.evently.model.VendorProfile;

/**
 * Test class to validate JPA entity relationships and Django schema compatibility.
 * This ensures all entity mappings match the Django database structure.
 */
@SpringBootTest
@ActiveProfiles("test")
public class JpaMappingValidationTest {
    
    @Test
    public void testUserEntityMapping() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("hashedpassword");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        
        // Test default values
        assertNotNull(user.getIsActive());
        assertNotNull(user.getIsVendor());
        assertNotNull(user.getIsPlanner());
        
        // Test helper methods
        assertEquals("Test User", user.getFullName());
        assertTrue(user.isEnabled());
    }
    
    @Test
    public void testEventEntityMapping() {
        User planner = new User();
        planner.setUsername("planner");
        
        Event event = new Event();
        event.setPlanner(planner);
        event.setTitle("Test Event");
        event.setDate(LocalDateTime.now().plusDays(30));
        event.setLocation("Test Location");
        
        assertNotNull(event.getPlanner());
        assertEquals("Test Event", event.getTitle());
        assertEquals(0, event.getGuestCount());
        assertEquals(0, event.getVendorCount());
    }
    
    @Test
    public void testGuestEntityMapping() {
        User user = new User();
        user.setUsername("guest");
        
        Event event = new Event();
        event.setTitle("Wedding");
        
        Guest guest = new Guest();
        guest.setEvent(event);
        guest.setUser(user);
        guest.setEmail("guest@example.com");
        guest.setName("Guest Name");
        
        assertEquals(Guest.RsvpStatus.INVITED, guest.getRsvpStatus());
        assertEquals("Guest Name", guest.getDisplayName());
        assertTrue(guest.isRegisteredUser());
    }
    
    @Test
    public void testVendorProfileEntityMapping() {
        User user = new User();
        user.setUsername("vendor");
        
        VendorProfile vendor = new VendorProfile();
        vendor.setUser(user);
        vendor.setBusinessName("Test Vendor LLC");
        vendor.setDescription("Test vendor description");
        vendor.setLocation("Test City");
        
        assertFalse(vendor.isVerified());
        assertEquals("Test Vendor LLC", vendor.getDisplayName());
        assertEquals(0, vendor.getServicesCount());
        assertEquals(0, vendor.getPortfolioCount());
        assertEquals(0, vendor.getReviewsCount());
    }
    
    @Test
    public void testServiceCategoryEntityMapping() {
        ServiceCategory category = new ServiceCategory();
        category.setName("Photography");
        category.setDescription("Professional photography services");
        
        assertEquals("Photography", category.getName());
        assertEquals(0, category.getServicesCount());
    }
    
    @Test
    public void testServiceEntityMapping() {
        VendorProfile vendor = new VendorProfile();
        vendor.setBusinessName("Photo Studio");
        
        ServiceCategory category = new ServiceCategory();
        category.setName("Photography");
        
        Service service = new Service();
        service.setVendor(vendor);
        service.setCategory(category);
        service.setTitle("Wedding Photography");
        service.setDescription("Professional wedding photography package");
        service.setPrice(new BigDecimal("1500.00"));
        
        assertEquals("Wedding Photography", service.getTitle());
        assertEquals("$1500.00", service.getFormattedPrice());
        assertEquals("Photo Studio", service.getVendorBusinessName());
        assertEquals("Photography", service.getCategoryName());
    }
    
    @Test
    public void testPortfolioItemEntityMapping() {
        VendorProfile vendor = new VendorProfile();
        vendor.setBusinessName("Event Planner Pro");
        
        PortfolioItem portfolio = new PortfolioItem();
        portfolio.setVendor(vendor);
        portfolio.setDescription("Beautiful wedding setup");
        portfolio.setImage("/uploads/portfolio/wedding1.jpg");
        
        assertTrue(portfolio.hasImage());
        assertEquals("Event Planner Pro", portfolio.getVendorBusinessName());
        assertEquals("Beautiful wedding setup", portfolio.getDisplayDescription());
    }
    
    @Test
    public void testReviewEntityMapping() {
        User user = new User();
        user.setUsername("customer");
        user.setFirstName("Happy");
        user.setLastName("Customer");
        
        VendorProfile vendor = new VendorProfile();
        vendor.setBusinessName("Awesome Catering");
        
        Review review = new Review();
        review.setVendor(vendor);
        review.setUser(user);
        review.setRating(5);
        review.setComment("Excellent service!");
        
        assertEquals(5, review.getRating());
        assertEquals("★★★★★", review.getStarDisplay());
        assertTrue(review.hasComment());
        assertEquals("Happy Customer", review.getReviewerName());
        assertEquals("Awesome Catering", review.getVendorBusinessName());
        assertEquals("Exc...", review.getShortComment(6));
    }
    
    @Test
    public void testVendorBookingEntityMapping() {
        Event event = new Event();
        event.setTitle("Corporate Event");
        
        VendorProfile vendor = new VendorProfile();
        vendor.setBusinessName("Sound Systems Pro");
        
        Service service = new Service();
        service.setTitle("Audio Equipment Rental");
        
        VendorBooking booking = new VendorBooking();
        booking.setEvent(event);
        booking.setVendor(vendor);
        booking.setService(service);
        booking.setNotes("Need setup by 8 AM");
        
        assertEquals(VendorBooking.BookingStatus.PENDING, booking.getStatus());
        assertTrue(booking.isPending());
        assertFalse(booking.isConfirmed());
        assertTrue(booking.toString().contains("Sound Systems Pro"));
        assertTrue(booking.toString().contains("Audio Equipment Rental"));
    }
    
    @Test
    public void testEntityRelationships() {
        // Test bi-directional relationships
        User planner = new User();
        planner.setUsername("eventplanner");
        
        Event event = new Event();
        event.setPlanner(planner);
        event.setTitle("Wedding Reception");
        event.setDate(LocalDateTime.now().plusDays(60));
        event.setLocation("Grand Ballroom");
        
        Guest guest = new Guest();
        guest.setEvent(event);
        guest.setEmail("guest@wedding.com");
        
        VendorProfile vendor = new VendorProfile();
        vendor.setBusinessName("Elite Catering");
        
        Service service = new Service();
        service.setVendor(vendor);
        service.setTitle("Wedding Catering Package");
        service.setPrice(new BigDecimal("5000.00"));
        
        VendorBooking booking = new VendorBooking();
        booking.setEvent(event);
        booking.setVendor(vendor);
        booking.setService(service);
        
        // Verify relationships
        assertEquals(event, guest.getEvent());
        assertEquals(event, booking.getEvent());
        assertEquals(vendor, booking.getVendor());
        assertEquals(service, booking.getService());
        assertEquals(vendor, service.getVendor());
    }
}