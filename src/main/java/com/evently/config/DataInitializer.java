package com.evently.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.evently.model.Review;
import com.evently.model.User;
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
        try {
            // Only initialize if database is empty
            if (userRepository.count() == 0) {
                initializeBasicData();
            }
        } catch (Exception e) {
            System.err.println("ERROR in DataInitializer: " + e.getMessage());
            e.printStackTrace();
            
            System.err.println("Continuing without sample data...");
        }
    }

    private void initializeBasicData() {
        System.out.println("Adding basic sample data...");

        try {
            
            User planner1 = createUser("eventplanner", "planner@gmail.com", "Event", "Planner", false, true);
            User planner2 = createUser("weddingpro", "wedding@gmail.com", "Wedding", "Professional", false, true);
            User photographer = createUser("photographer1", "photo1@gmail.com", "John", "Smith", true, false);
            User caterer = createUser("caterer1", "catering1@gmail.com", "Jane", "Doe", true, false);
            User dj = createUser("dj1", "dj1@gmail.com", "David", "Brown", true, false);
            User decorator = createUser("decorator1", "decor1@gmail.com", "Emma", "Davis", true, false);
            User florist = createUser("florist1", "florist1@gmail.com", "Maria", "Taylor", true, false);
            User customer1 = createUser("customer1", "customer1@gmail.com", "Alice", "Johnson", false, false);
            User customer2 = createUser("customer2", "customer2@gmail.com", "Bob", "Wilson", false, false);
            User customer3 = createUser("customer3", "customer3@gmail.com", "Carol", "Davis", false, false);
            VendorProfile photographerProfile = createVendorProfile(photographer, "Smith Photography Studio", 
                "Professional event and wedding photography with 10+ years experience", 
                "Downtown, City Center", "john.smith@gmail.com | (555) 123-4567");
                
            VendorProfile catererProfile = createVendorProfile(caterer, "Jane's Gourmet Catering", 
                "Full-service catering for weddings, corporate events, and special occasions", 
                "Westside, Business District", "jane.doe@gmail.com | (555) 234-5678");
                
            VendorProfile djProfile = createVendorProfile(dj, "DJ Dave's Entertainment", 
                "Professional DJ services with lighting and sound systems for all events", 
                "City Center", "david.brown@gmail.com | (555) 345-6789");
                
            VendorProfile decoratorProfile = createVendorProfile(decorator, "Emma's Event Designs", 
                "Creative event decoration and styling for memorable occasions", 
                "Arts District", "emma.davis@gmail.com | (555) 456-7890");
                
            VendorProfile floristProfile = createVendorProfile(florist, "Taylor's Floral Boutique", 
                "Beautiful flower arrangements for weddings and events", 
                "Garden District", "maria.taylor@gmail.com | (555) 567-8901");

            // Add reviews for each vendor
            createReview(photographerProfile, customer1, 5, "Absolutely amazing photographer! Captured every moment perfectly.");
            createReview(photographerProfile, customer2, 4, "Great quality photos, very professional service.");
            
            createReview(catererProfile, customer1, 5, "The food was incredible! All our guests were impressed.");
            createReview(catererProfile, customer3, 5, "Outstanding service and delicious meals. Highly recommend!");
            
            createReview(djProfile, customer2, 4, "Kept the party going all night! Great music selection.");
            createReview(djProfile, customer3, 5, "Perfect entertainment for our event. Very professional.");
            
            createReview(decoratorProfile, customer1, 5, "Emma transformed our venue into something magical!");
            createReview(decoratorProfile, customer2, 4, "Beautiful decorations that matched our theme perfectly.");
            
            createReview(floristProfile, customer2, 5, "Stunning floral arrangements that exceeded our expectations.");
            createReview(floristProfile, customer3, 4, "Fresh, beautiful flowers. Great attention to detail.");

            System.out.println("sample data:");
            System.out.println("  10 Users, 5 Vendor Profiles, 10 Reviews");
            System.out.println("   PLANNERS:");
            System.out.println("   - eventplanner / password123");
            System.out.println("   - weddingpro / password123");
            System.out.println("   VENDORS (with profiles & reviews):");
            System.out.println("   - photographer1 / password123 (4.5★ avg rating)");
            System.out.println("   - caterer1 / password123 (5.0★ avg rating)");
            System.out.println("   - dj1 / password123 (4.5★ avg rating)");
            System.out.println("   - decorator1 / password123 (4.5★ avg rating)");
            System.out.println("   - florist1 / password123 (4.5★ avg rating)");
            System.out.println("   CUSTOMERS:");
            System.out.println("   - customer1 / password123");
            System.out.println("   - customer2 / password123");
            System.out.println("   - customer3 / password123");

        } catch (Exception e) {
            System.err.println("Error creating sample data: " + e.getMessage());
            e.printStackTrace();
        }
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
        user.setIsActive(true);
        user.setDateJoined(LocalDateTime.now());
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
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        return vendorProfileRepository.save(profile);
    }

    private Review createReview(VendorProfile vendor, User user, int rating, String comment) {
        Review review = new Review();
        review.setVendor(vendor);
        review.setUser(user);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }}
