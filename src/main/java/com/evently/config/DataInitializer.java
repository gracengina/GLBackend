package com.evently.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.evently.model.Event;
import com.evently.model.Guest;
import com.evently.model.PortfolioItem;
import com.evently.model.Review;
import com.evently.model.Service;
import com.evently.model.ServiceCategory;
import com.evently.model.User;
import com.evently.model.VendorBooking;
import com.evently.model.VendorProfile;
import com.evently.repository.EventRepository;
import com.evently.repository.GuestRepository;
import com.evently.repository.PortfolioItemRepository;
import com.evently.repository.ReviewRepository;
import com.evently.repository.ServiceCategoryRepository;
import com.evently.repository.ServiceRepository;
import com.evently.repository.UserRepository;
import com.evently.repository.VendorBookingRepository;
import com.evently.repository.VendorProfileRepository;

/**
 * Data initialization for fresh database.
 * Creates sample data to test the application functionality.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VendorProfileRepository vendorProfileRepository;
    
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private EventRepository eventRepository;
    
    @Autowired
    private GuestRepository guestRepository;
    
    @Autowired
    private VendorBookingRepository vendorBookingRepository;
    
    @Autowired
    private PortfolioItemRepository portfolioItemRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty
        if (userRepository.count() == 0) {
            initializeData();
        }
    }

    private void initializeData() {
        System.out.println("Initializing fresh database with sample data...");

        // Create Service Categories
        ServiceCategory photography = createServiceCategory("Photography", "Professional photography services");
        ServiceCategory catering = createServiceCategory("Catering", "Food and beverage services");
        createServiceCategory("Music & Entertainment", "DJ, live music, and entertainment");
        createServiceCategory("Decoration", "Event decoration and setup");

        // Create Users
        User planner1 = createUser("eventplanner", "planner@evently.com", "Event", "Planner", false, true);
        User planner2 = createUser("weddingpro", "wedding@evently.com", "Wedding", "Professional", false, true);
        User vendor1 = createUser("photographer", "photo@vendor.com", "John", "Smith", true, false);
        User vendor2 = createUser("caterer", "catering@vendor.com", "Jane", "Doe", true, false);
        User customer1 = createUser("customer1", "customer1@example.com", "Alice", "Johnson", false, false);
        User customer2 = createUser("customer2", "customer2@example.com", "Bob", "Wilson", false, false);

        // Create Vendor Profiles
        VendorProfile photoVendor = createVendorProfile(vendor1, "Smith Photography Studio", 
            "Professional wedding and event photography", "New York, NY", "555-0123");
        VendorProfile cateringVendor = createVendorProfile(vendor2, "Elegant Catering Services", 
            "Premium catering for all occasions", "Brooklyn, NY", "555-0456");

        // Create Services
        Service weddingPhoto = createService(photoVendor, photography, "Wedding Photography Package", 
            "Complete wedding day photography with 500+ edited photos", new BigDecimal("2500.00"));
        Service eventPhoto = createService(photoVendor, photography, "Corporate Event Photography", 
            "Professional event photography for corporate functions", new BigDecimal("800.00"));
        Service weddingCatering = createService(cateringVendor, catering, "Wedding Catering Package", 
            "Full-service wedding catering for up to 150 guests", new BigDecimal("4500.00"));
        createService(cateringVendor, catering, "Corporate Lunch Catering", 
            "Business lunch catering for meetings and events", new BigDecimal("25.00"));

        // Create Events
        Event wedding = createEvent(planner1, "Smith-Johnson Wedding", 
            "Beautiful outdoor wedding ceremony and reception", 
            LocalDateTime.now().plusDays(45), "Central Park, New York");
        Event corporate = createEvent(planner2, "TechCorp Annual Meeting", 
            "Annual company meeting and team building event", 
            LocalDateTime.now().plusDays(30), "Marriott Hotel, Manhattan");

        // Create Guests
        createGuest(wedding, customer1, "Alice Johnson", "alice@example.com", Guest.RsvpStatus.ATTENDING);
        createGuest(wedding, customer2, "Bob Wilson", "bob@example.com", Guest.RsvpStatus.INVITED);
        createGuest(wedding, null, null, "guest1@wedding.com", Guest.RsvpStatus.ATTENDING);
        createGuest(corporate, customer1, "Alice Johnson", "alice@company.com", Guest.RsvpStatus.ATTENDING);

        // Create Vendor Bookings
        createVendorBooking(wedding, photoVendor, weddingPhoto, VendorBooking.BookingStatus.CONFIRMED, 
            "Setup at 10 AM, ceremony at 2 PM");
        createVendorBooking(wedding, cateringVendor, weddingCatering, VendorBooking.BookingStatus.CONFIRMED, 
            "Service for 120 guests, vegetarian options included");
        createVendorBooking(corporate, photoVendor, eventPhoto, VendorBooking.BookingStatus.PENDING, 
            "Corporate headshots and event coverage");

        // Create Portfolio Items
        createPortfolioItem(photoVendor, "/images/portfolio/wedding1.jpg", "Beautiful beach wedding ceremony");
        createPortfolioItem(photoVendor, "/images/portfolio/corporate1.jpg", "Professional corporate event");
        createPortfolioItem(cateringVendor, "/images/portfolio/catering1.jpg", "Elegant wedding reception setup");

        // Create Reviews
        createReview(photoVendor, customer1, 5, "Amazing photographer! Captured our special day perfectly.");
        createReview(photoVendor, customer2, 4, "Professional service and great quality photos.");
        createReview(cateringVendor, customer1, 5, "Delicious food and excellent service. Highly recommended!");

        System.out.println("Database initialization completed successfully!");
        System.out.println("Created:");
        System.out.println("- 4 Service Categories");
        System.out.println("- 6 Users (2 planners, 2 vendors, 2 customers)");
        System.out.println("- 2 Vendor Profiles");
        System.out.println("- 4 Services");
        System.out.println("- 2 Events");
        System.out.println("- 4 Guests");
        System.out.println("- 3 Vendor Bookings");
        System.out.println("- 3 Portfolio Items");
        System.out.println("- 3 Reviews");
    }

    private ServiceCategory createServiceCategory(String name, String description) {
        ServiceCategory category = new ServiceCategory();
        category.setName(name);
        category.setDescription(description);
        return serviceCategoryRepository.save(category);
    }

    private User createUser(String username, String email, String firstName, String lastName, 
                           boolean isVendor, boolean isPlanner) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode("password123"));
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setIsVendor(isVendor);
        user.setIsPlanner(isPlanner);
        return userRepository.save(user);
    }

    private VendorProfile createVendorProfile(User user, String businessName, String description, 
                                            String location, String contactInfo) {
        VendorProfile profile = new VendorProfile();
        profile.setUser(user);
        profile.setBusinessName(businessName);
        profile.setDescription(description);
        profile.setLocation(location);
        profile.setContactInfo(contactInfo);
        profile.setIsVerified(true);
        return vendorProfileRepository.save(profile);
    }

    private Service createService(VendorProfile vendor, ServiceCategory category, String title, 
                                String description, BigDecimal price) {
        Service service = new Service();
        service.setVendor(vendor);
        service.setCategory(category);
        service.setTitle(title);
        service.setDescription(description);
        service.setPrice(price);
        return serviceRepository.save(service);
    }

    private Event createEvent(User planner, String title, String description, 
                            LocalDateTime date, String location) {
        Event event = new Event();
        event.setPlanner(planner);
        event.setTitle(title);
        event.setDescription(description);
        event.setDate(date);
        event.setLocation(location);
        return eventRepository.save(event);
    }

    private Guest createGuest(Event event, User user, String name, String email, Guest.RsvpStatus status) {
        Guest guest = new Guest();
        guest.setEvent(event);
        guest.setUser(user);
        guest.setName(name);
        guest.setEmail(email);
        guest.setRsvpStatus(status);
        return guestRepository.save(guest);
    }

    private VendorBooking createVendorBooking(Event event, VendorProfile vendor, Service service, 
                                            VendorBooking.BookingStatus status, String notes) {
        VendorBooking booking = new VendorBooking();
        booking.setEvent(event);
        booking.setVendor(vendor);
        booking.setService(service);
        booking.setStatus(status);
        booking.setNotes(notes);
        return vendorBookingRepository.save(booking);
    }

    private PortfolioItem createPortfolioItem(VendorProfile vendor, String image, String description) {
        PortfolioItem item = new PortfolioItem();
        item.setVendor(vendor);
        item.setImage(image);
        item.setDescription(description);
        return portfolioItemRepository.save(item);
    }

    private Review createReview(VendorProfile vendor, User user, int rating, String comment) {
        Review review = new Review();
        review.setVendor(vendor);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }
}