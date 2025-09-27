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

/**
 * Data initialization for fresh database.
 * Creates minimal sample data to test the application functionality.
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
        try {
            // Only initialize if database is empty
            if (userRepository.count() == 0) {
                initializeBasicData();
            }
        } catch (Exception e) {
            System.err.println("ERROR in DataInitializer: " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception to prevent startup failure
            System.err.println("Continuing without sample data...");
        }
    }

    private void initializeBasicData() {
        System.out.println("Adding basic sample data...");

        try {
            // Create 2 planners
            User planner1 = createUser("eventplanner", "planner@evently.com", "Event", "Planner", false, true);
            User planner2 = createUser("weddingpro", "wedding@evently.com", "Wedding", "Professional", false, true);
            
            // Create 5 vendors
            User photographer = createUser("photographer1", "photo1@vendor.com", "John", "Smith", true, false);
            User caterer = createUser("caterer1", "catering1@vendor.com", "Jane", "Doe", true, false);
            User dj = createUser("dj1", "dj1@vendor.com", "David", "Brown", true, false);
            User decorator = createUser("decorator1", "decor1@vendor.com", "Emma", "Davis", true, false);
            User florist = createUser("florist1", "florist1@vendor.com", "Maria", "Taylor", true, false);
            
            // Create 3 customers
            User customer1 = createUser("customer1", "customer1@example.com", "Alice", "Johnson", false, false);
            User customer2 = createUser("customer2", "customer2@example.com", "Bob", "Wilson", false, false);
            User customer3 = createUser("customer3", "customer3@example.com", "Carol", "Davis", false, false);

            // Create vendor profiles for each vendor
            VendorProfile photographerProfile = createVendorProfile(photographer, "Smith Photography Studio", 
                "Professional event and wedding photography with 10+ years experience", 
                "Downtown, City Center", "john@smithphoto.com | (555) 123-4567");
                
            VendorProfile catererProfile = createVendorProfile(caterer, "Jane's Gourmet Catering", 
                "Full-service catering for weddings, corporate events, and special occasions", 
                "Westside, Business District", "jane@gourmetcatering.com | (555) 234-5678");
                
            VendorProfile djProfile = createVendorProfile(dj, "DJ Dave's Entertainment", 
                "Professional DJ services with lighting and sound systems for all events", 
                "City Center", "david@djdave.com | (555) 345-6789");
                
            VendorProfile decoratorProfile = createVendorProfile(decorator, "Emma's Event Designs", 
                "Creative event decoration and styling for memorable occasions", 
                "Arts District", "emma@eventdesigns.com | (555) 456-7890");
                
            VendorProfile floristProfile = createVendorProfile(florist, "Taylor's Floral Boutique", 
                "Beautiful flower arrangements for weddings and events", 
                "Garden District", "maria@floralboutique.com | (555) 567-8901");

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

            System.out.println("✅ Successfully added complete sample data:");
            System.out.println("   📊 10 Users, 5 Vendor Profiles, 10 Reviews");
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
    }

    /*
     * OLD COMPLEX INITIALIZATION METHOD - DISABLED
     * This method was causing startup issues, so we're using the simple version above
     */
    /*
    private void initializeData() {
        System.out.println("Initializing fresh database with comprehensive sample data...");

        // Create Service Categories
        ServiceCategory photography = createServiceCategory("Photography", "Professional photography services");
        ServiceCategory catering = createServiceCategory("Catering", "Food and beverage services");
        ServiceCategory music = createServiceCategory("Music & Entertainment", "DJ, live music, and entertainment");
        ServiceCategory decoration = createServiceCategory("Decoration", "Event decoration and setup");
        ServiceCategory venue = createServiceCategory("Venues", "Event venues and locations");
        ServiceCategory flowers = createServiceCategory("Florists", "Wedding and event flower arrangements");

        // Create Planners (5 planners)
        User planner1 = createUser("eventplanner", "planner@evently.com", "Event", "Planner", false, true);
        User planner2 = createUser("weddingpro", "wedding@evently.com", "Wedding", "Professional", false, true);
        User planner3 = createUser("corporatepro", "corporate@evently.com", "Corporate", "Events", false, true);
        User planner4 = createUser("partyplanner", "party@evently.com", "Party", "Master", false, true);
        User planner5 = createUser("luxuryevents", "luxury@evently.com", "Luxury", "Events", false, true);

        // Create Vendors (20 vendors)
        User vendor1 = createUser("photographer1", "photo1@vendor.com", "John", "Smith", true, false);
        User vendor2 = createUser("caterer1", "catering1@vendor.com", "Jane", "Doe", true, false);
        User vendor3 = createUser("photographer2", "photo2@vendor.com", "Mike", "Johnson", true, false);
        User vendor4 = createUser("caterer2", "catering2@vendor.com", "Sarah", "Wilson", true, false);
        User vendor5 = createUser("dj1", "dj1@vendor.com", "David", "Brown", true, false);
        User vendor6 = createUser("decorator1", "decor1@vendor.com", "Emma", "Davis", true, false);
        User vendor7 = createUser("photographer3", "photo3@vendor.com", "Chris", "Miller", true, false);
        User vendor8 = createUser("caterer3", "catering3@vendor.com", "Lisa", "Garcia", true, false);
        User vendor9 = createUser("dj2", "dj2@vendor.com", "Ryan", "Martinez", true, false);
        User vendor10 = createUser("decorator2", "decor2@vendor.com", "Ashley", "Rodriguez", true, false);
        User vendor11 = createUser("venue1", "venue1@vendor.com", "Robert", "Anderson", true, false);
        User vendor12 = createUser("florist1", "florist1@vendor.com", "Maria", "Taylor", true, false);
        User vendor13 = createUser("photographer4", "photo4@vendor.com", "James", "Thomas", true, false);
        User vendor14 = createUser("caterer4", "catering4@vendor.com", "Jennifer", "Jackson", true, false);
        User vendor15 = createUser("dj3", "dj3@vendor.com", "Michael", "White", true, false);
        User vendor16 = createUser("decorator3", "decor3@vendor.com", "Amanda", "Harris", true, false);
        User vendor17 = createUser("venue2", "venue2@vendor.com", "Daniel", "Martin", true, false);
        User vendor18 = createUser("florist2", "florist2@vendor.com", "Jessica", "Thompson", true, false);
        User vendor19 = createUser("photographer5", "photo5@vendor.com", "Kevin", "Garcia", true, false);
        User vendor20 = createUser("caterer5", "catering5@vendor.com", "Michelle", "Martinez", true, false);

        // Create Customers (15 customers)
        User customer1 = createUser("customer1", "customer1@example.com", "Alice", "Johnson", false, false);
        User customer2 = createUser("customer2", "customer2@example.com", "Bob", "Wilson", false, false);
        User customer3 = createUser("customer3", "customer3@example.com", "Carol", "Davis", false, false);
        User customer4 = createUser("customer4", "customer4@example.com", "David", "Miller", false, false);
        User customer5 = createUser("customer5", "customer5@example.com", "Eva", "Brown", false, false);
        User customer6 = createUser("customer6", "customer6@example.com", "Frank", "Garcia", false, false);
        User customer7 = createUser("customer7", "customer7@example.com", "Grace", "Martinez", false, false);
        User customer8 = createUser("customer8", "customer8@example.com", "Henry", "Rodriguez", false, false);
        User customer9 = createUser("customer9", "customer9@example.com", "Iris", "Anderson", false, false);
        User customer10 = createUser("customer10", "customer10@example.com", "Jack", "Taylor", false, false);
        User customer11 = createUser("customer11", "customer11@example.com", "Kate", "Thomas", false, false);
        User customer12 = createUser("customer12", "customer12@example.com", "Leo", "Jackson", false, false);
        User customer13 = createUser("customer13", "customer13@example.com", "Mia", "White", false, false);
        User customer14 = createUser("customer14", "customer14@example.com", "Noah", "Harris", false, false);
        User customer15 = createUser("customer15", "customer15@example.com", "Olivia", "Martin", false, false);

        // Create Vendor Profiles (20 vendor profiles)
        VendorProfile photoVendor1 = createVendorProfile(vendor1, "Smith Photography Studio", 
            "Professional wedding and event photography", "New York, NY", "555-0123");
        VendorProfile cateringVendor1 = createVendorProfile(vendor2, "Elegant Catering Services", 
            "Premium catering for all occasions", "Brooklyn, NY", "555-0456");
        VendorProfile photoVendor2 = createVendorProfile(vendor3, "Johnson Photography", 
            "Creative photography for all events", "Manhattan, NY", "555-0789");
        VendorProfile cateringVendor2 = createVendorProfile(vendor4, "Gourmet Delights Catering", 
            "Exquisite culinary experiences", "Queens, NY", "555-0321");
        VendorProfile djVendor1 = createVendorProfile(vendor5, "Beat Masters DJ Services", 
            "Professional DJ services for all events", "Bronx, NY", "555-0654");
        VendorProfile decorVendor1 = createVendorProfile(vendor6, "Divine Decorations", 
            "Stunning event decorations and setup", "Staten Island, NY", "555-0987");
        VendorProfile photoVendor3 = createVendorProfile(vendor7, "Miller Photo Art", 
            "Artistic event and portrait photography", "Long Island, NY", "555-0147");
        VendorProfile cateringVendor3 = createVendorProfile(vendor8, "Garcia's Catering Co", 
            "Authentic cuisine for special events", "Jersey City, NJ", "555-0258");
        VendorProfile djVendor2 = createVendorProfile(vendor9, "Martinez Music & Entertainment", 
            "DJ and live entertainment services", "Hoboken, NJ", "555-0369");
        VendorProfile decorVendor2 = createVendorProfile(vendor10, "Rodriguez Event Design", 
            "Creative event design and decoration", "Newark, NJ", "555-0741");
        VendorProfile venueVendor1 = createVendorProfile(vendor11, "Grand Ballroom Venues", 
            "Elegant venues for weddings and corporate events", "Manhattan, NY", "555-0852");
        VendorProfile floristVendor1 = createVendorProfile(vendor12, "Taylor's Floral Designs", 
            "Beautiful wedding and event floral arrangements", "Brooklyn, NY", "555-0963");
        VendorProfile photoVendor4 = createVendorProfile(vendor13, "Thomas Photography Studio", 
            "Professional event documentation", "Queens, NY", "555-0174");
        VendorProfile cateringVendor4 = createVendorProfile(vendor14, "Jackson Culinary Services", 
            "Fine dining catering experiences", "Bronx, NY", "555-0285");
        VendorProfile djVendor3 = createVendorProfile(vendor15, "White Entertainment Group", 
            "Complete entertainment solutions", "Staten Island, NY", "555-0396");
        VendorProfile decorVendor3 = createVendorProfile(vendor16, "Harris Event Styling", 
            "Luxury event styling and decor", "Long Island, NY", "555-0507");
        VendorProfile venueVendor2 = createVendorProfile(vendor17, "Martin Manor & Gardens", 
            "Beautiful outdoor and indoor event spaces", "Westchester, NY", "555-0618");
        VendorProfile floristVendor2 = createVendorProfile(vendor18, "Thompson Flower Studio", 
            "Custom floral designs for special occasions", "Fairfield, CT", "555-0729");
        VendorProfile photoVendor5 = createVendorProfile(vendor19, "Garcia Visual Arts", 
            "Contemporary event photography", "New Haven, CT", "555-0831");
        VendorProfile cateringVendor5 = createVendorProfile(vendor20, "Martinez Premium Catering", 
            "Upscale catering for discerning clients", "Stamford, CT", "555-0942");

        // Create Services (40+ services across all vendors)
        createService(photoVendor1, photography, "Wedding Photography", 
            "Complete wedding day photography package", new BigDecimal("2500"));
        createService(photoVendor1, photography, "Portrait Sessions", 
            "Professional portrait photography", new BigDecimal("500"));
        createService(photoVendor2, photography, "Event Photography", 
            "Corporate and social event photography", new BigDecimal("1500"));
        createService(photoVendor2, photography, "Engagement Shoots", 
            "Romantic engagement photography", new BigDecimal("800"));
        createService(photoVendor3, photography, "Family Photography", 
            "Family portrait and lifestyle photography", new BigDecimal("600"));
        createService(photoVendor4, photography, "Corporate Headshots", 
            "Professional business portraits", new BigDecimal("300"));
        createService(photoVendor5, photography, "Birthday Party Photography", 
            "Children and adult birthday celebrations", new BigDecimal("750"));

        Service weddingCatering1 = createService(cateringVendor1, catering, "Wedding Catering", 
            "Full-service wedding catering and bar", new BigDecimal("8500"));
        createService(cateringVendor1, catering, "Corporate Lunch Catering", 
            "Business meeting and conference catering", new BigDecimal("1200"));
        Service cocktailCatering = createService(cateringVendor2, catering, "Cocktail Party Catering", 
            "Elegant appetizers and cocktail service", new BigDecimal("3500"));
        createService(cateringVendor2, catering, "Birthday Party Catering", 
            "Party food and cake service", new BigDecimal("2000"));
        Service buffetCatering = createService(cateringVendor3, catering, "Buffet Catering", 
            "Large group buffet service", new BigDecimal("2500"));
        createService(cateringVendor4, catering, "Fine Dining Service", 
            "Upscale plated dinner service", new BigDecimal("4500"));
        createService(cateringVendor5, catering, "Brunch Catering", 
            "Weekend brunch and breakfast service", new BigDecimal("1800"));

        Service weddingDJ1 = createService(djVendor1, music, "Wedding DJ Services", 
            "Complete wedding reception entertainment", new BigDecimal("1500"));
        createService(djVendor1, music, "Corporate Event DJ", 
            "Professional audio and music for business events", new BigDecimal("800"));
        Service birthdayDJ = createService(djVendor2, music, "Birthday Party DJ", 
            "Party music and entertainment", new BigDecimal("600"));
        createService(djVendor2, music, "School Dance DJ", 
            "Age-appropriate music for school events", new BigDecimal("500"));
        Service anniversaryDJ = createService(djVendor3, music, "Anniversary Party DJ", 
            "Special anniversary celebration music", new BigDecimal("750"));

        Service weddingDecor1 = createService(decorVendor1, decoration, "Wedding Decoration", 
            "Complete wedding venue decoration", new BigDecimal("3500"));
        Service birthdayDecor = createService(decorVendor1, decoration, "Birthday Party Decor", 
            "Themed birthday party decorations", new BigDecimal("800"));
        createService(decorVendor2, decoration, "Corporate Event Setup", 
            "Professional business event decoration", new BigDecimal("2000"));
        createService(decorVendor2, decoration, "Baby Shower Decor", 
            "Beautiful baby shower decorations", new BigDecimal("600"));
        createService(decorVendor3, decoration, "Anniversary Decoration", 
            "Elegant anniversary celebration decor", new BigDecimal("1200"));

        createService(venueVendor1, venue, "Grand Ballroom Rental", 
            "Elegant ballroom for up to 300 guests", new BigDecimal("5000"));
        createService(venueVendor1, venue, "Intimate Reception Hall", 
            "Cozy reception space for up to 100 guests", new BigDecimal("2500"));
        createService(venueVendor2, venue, "Garden Wedding Venue", 
            "Beautiful outdoor wedding ceremony and reception", new BigDecimal("4000"));
        createService(venueVendor2, venue, "Corporate Conference Room", 
            "Professional meeting and conference space", new BigDecimal("1200"));

        Service bridalFlowers = createService(floristVendor1, flowers, "Bridal Bouquet & Arrangements", 
            "Wedding flowers and bridal bouquet", new BigDecimal("1500"));
        createService(floristVendor1, flowers, "Centerpiece Arrangements", 
            "Table centerpieces for events", new BigDecimal("800"));
        createService(floristVendor2, flowers, "Ceremony Arch Flowers", 
            "Beautiful floral arch for ceremonies", new BigDecimal("1200"));
        createService(floristVendor2, flowers, "Corporate Event Flowers", 
            "Professional floral arrangements for business", new BigDecimal("600"));

        // Create Events (25 events)
        Event weddingEvent1 = createEvent(planner1, "Johnson-Wilson Wedding", 
            "Elegant garden wedding ceremony and reception", 
            LocalDateTime.now().plusDays(45), "Central Park, NY");
        Event weddingEvent2 = createEvent(planner2, "Davis-Miller Wedding", 
            "Luxury indoor wedding celebration", 
            LocalDateTime.now().plusDays(60), "Plaza Hotel, NY");
        Event corpEvent1 = createEvent(planner3, "TechCorp Annual Meeting", 
            "Corporate annual meeting and awards ceremony", 
            LocalDateTime.now().plusDays(30), "Marriott Hotel, NY");
        Event corpEvent2 = createEvent(planner3, "Quarterly Business Review", 
            "Executive quarterly business meeting", 
            LocalDateTime.now().plusDays(15), "Hilton Hotel, NY");
        Event birthdayEvent1 = createEvent(planner4, "Eva's 30th Birthday Bash", 
            "Surprise birthday party celebration", 
            LocalDateTime.now().plusDays(20), "Private Venue, Brooklyn");
        Event birthdayEvent2 = createEvent(planner4, "Frank's 40th Birthday", 
            "Milestone birthday celebration", 
            LocalDateTime.now().plusDays(25), "Rooftop Bar, Manhattan");
        Event anniversaryEvent1 = createEvent(planner5, "Garcia Anniversary Celebration", 
            "25th wedding anniversary party", 
            LocalDateTime.now().plusDays(35), "Country Club, NY");
        Event weddingEvent3 = createEvent(planner1, "Rodriguez Wedding", 
            "Traditional wedding ceremony", 
            LocalDateTime.now().plusDays(70), "St. Patrick's Cathedral, NY");
        Event corpEvent3 = createEvent(planner3, "Innovation Summit 2024", 
            "Technology innovation conference", 
            LocalDateTime.now().plusDays(40), "Convention Center, NY");
        Event birthdayEvent3 = createEvent(planner4, "Jack's Sweet 16", 
            "Teenager birthday celebration", 
            LocalDateTime.now().plusDays(18), "Community Center, Queens");
        Event weddingEvent4 = createEvent(planner2, "Thomas Wedding", 
            "Beach-themed wedding reception", 
            LocalDateTime.now().plusDays(55), "Long Island Venue");
        Event corpEvent4 = createEvent(planner3, "Leadership Training Workshop", 
            "Executive leadership development", 
            LocalDateTime.now().plusDays(12), "Business Center, Manhattan");
        Event anniversaryEvent2 = createEvent(planner5, "White Anniversary Gala", 
            "50th golden wedding anniversary", 
            LocalDateTime.now().plusDays(50), "Grand Ballroom, NY");
        Event birthdayEvent4 = createEvent(planner4, "Noah's Graduation Party", 
            "College graduation celebration", 
            LocalDateTime.now().plusDays(22), "Private Home, Brooklyn");
        Event weddingEvent5 = createEvent(planner1, "Martin Wedding Extravaganza", 
            "Grand wedding with 200+ guests", 
            LocalDateTime.now().plusDays(80), "Luxury Hotel Ballroom, NY");

        // Additional events for comprehensive testing
        createEvent(planner2, "Baby Shower Celebration", 
            "Elegant baby shower party", 
            LocalDateTime.now().plusDays(28), "Private Garden, NY");
        createEvent(planner4, "Retirement Party", 
            "Farewell retirement celebration", 
            LocalDateTime.now().plusDays(33), "Restaurant Venue, NY");
        createEvent(planner5, "Engagement Party", 
            "Romantic engagement celebration", 
            LocalDateTime.now().plusDays(38), "Rooftop Venue, Manhattan");
        createEvent(planner1, "Charity Gala", 
            "Annual charity fundraising gala", 
            LocalDateTime.now().plusDays(65), "Museum Venue, NY");
        createEvent(planner3, "Product Launch Event", 
            "New product launch presentation", 
            LocalDateTime.now().plusDays(42), "Modern Gallery, Brooklyn");
        createEvent(planner2, "Holiday Party", 
            "Company holiday celebration", 
            LocalDateTime.now().plusDays(75), "Hotel Ballroom, NY");
        createEvent(planner4, "Housewarming Party", 
            "New home celebration", 
            LocalDateTime.now().plusDays(27), "Private Residence, Queens");
        createEvent(planner5, "Awards Ceremony", 
            "Industry awards and recognition", 
            LocalDateTime.now().plusDays(48), "Theater Venue, Manhattan");
        createEvent(planner1, "Class Reunion", 
            "10-year high school reunion", 
            LocalDateTime.now().plusDays(62), "Banquet Hall, NY");
        createEvent(planner3, "Networking Event", 
            "Professional networking mixer", 
            LocalDateTime.now().plusDays(16), "Business Lounge, NY");

        // Create comprehensive guest lists for events
        createGuest(weddingEvent1, customer2, "Bob Wilson", "bob@example.com", Guest.RsvpStatus.ATTENDING);
        createGuest(weddingEvent1, customer3, "Carol Davis", "carol@example.com", Guest.RsvpStatus.ATTENDING);
        createGuest(weddingEvent1, customer4, "David Miller", "david@example.com", Guest.RsvpStatus.INVITED);
        createGuest(weddingEvent2, customer1, "Alice Johnson", "alice@example.com", Guest.RsvpStatus.ATTENDING);
        createGuest(weddingEvent2, customer5, "Eva Brown", "eva@example.com", Guest.RsvpStatus.ATTENDING);
        createGuest(corpEvent1, customer6, "Frank Garcia", "frank@company.com", Guest.RsvpStatus.ATTENDING);
        createGuest(corpEvent1, customer7, "Grace Martinez", "grace@company.com", Guest.RsvpStatus.ATTENDING);
        createGuest(birthdayEvent1, customer8, "Henry Rodriguez", "henry@example.com", Guest.RsvpStatus.ATTENDING);
        createGuest(birthdayEvent1, customer9, "Iris Anderson", "iris@example.com", Guest.RsvpStatus.ATTENDING);
        createGuest(birthdayEvent1, customer10, "Jack Taylor", "jack@example.com", Guest.RsvpStatus.INVITED);

        // Create vendor bookings for events
        createVendorBooking(weddingEvent1, photoVendor1, createService(photoVendor1, photography, "Wedding Photo Package", "Complete wedding photography", new BigDecimal("2500")), VendorBooking.BookingStatus.CONFIRMED, 
            "Wedding photography - full day coverage");
        createVendorBooking(weddingEvent1, cateringVendor1, weddingCatering1, VendorBooking.BookingStatus.CONFIRMED, 
            "Wedding catering for 150 guests");
        createVendorBooking(weddingEvent1, djVendor1, weddingDJ1, VendorBooking.BookingStatus.CONFIRMED, 
            "Reception DJ services");
        createVendorBooking(weddingEvent2, photoVendor2, createService(photoVendor2, photography, "Luxury Wedding Photo", "Premium wedding photography", new BigDecimal("3000")), VendorBooking.BookingStatus.CONFIRMED, 
            "Luxury wedding photography");
        createVendorBooking(weddingEvent2, cateringVendor2, cocktailCatering, VendorBooking.BookingStatus.CONFIRMED, 
            "Premium catering service");
        createVendorBooking(corpEvent1, photoVendor3, createService(photoVendor3, photography, "Corp Event Photo", "Corporate event photography", new BigDecimal("1500")), VendorBooking.BookingStatus.PENDING, 
            "Corporate event photography");
        createVendorBooking(corpEvent1, cateringVendor3, buffetCatering, VendorBooking.BookingStatus.CONFIRMED, 
            "Business lunch catering");
        createVendorBooking(birthdayEvent1, decorVendor1, birthdayDecor, VendorBooking.BookingStatus.CONFIRMED, 
            "Birthday party decorations");
        createVendorBooking(birthdayEvent1, djVendor2, birthdayDJ, VendorBooking.BookingStatus.CONFIRMED, 
            "Birthday party entertainment");
        createVendorBooking(anniversaryEvent1, floristVendor1, bridalFlowers, VendorBooking.BookingStatus.CONFIRMED, 
            "Anniversary floral arrangements");

        // Create portfolio items for vendors
        createPortfolioItem(photoVendor1, "/images/portfolio/wedding_central_park.jpg", "Central Park wedding ceremony");
        createPortfolioItem(photoVendor1, "/images/portfolio/engagement_brooklyn.jpg", "Brooklyn Bridge engagement shoot");
        createPortfolioItem(photoVendor2, "/images/portfolio/luxury_wedding.jpg", "Luxury hotel wedding reception");
        createPortfolioItem(photoVendor3, "/images/portfolio/family_portraits.jpg", "Family portrait session in studio");
        createPortfolioItem(photoVendor4, "/images/portfolio/corporate_headshots.jpg", "Professional corporate headshots");
        createPortfolioItem(cateringVendor1, "/images/portfolio/wedding_buffet.jpg", "Elegant wedding buffet setup");
        createPortfolioItem(cateringVendor2, "/images/portfolio/cocktail_party.jpg", "Upscale cocktail party service");
        createPortfolioItem(cateringVendor3, "/images/portfolio/corporate_lunch.jpg", "Business lunch presentation");
        createPortfolioItem(decorVendor1, "/images/portfolio/wedding_decor.jpg", "Romantic wedding decoration");
        createPortfolioItem(decorVendor2, "/images/portfolio/corporate_setup.jpg", "Professional event setup");
        createPortfolioItem(djVendor1, "/images/portfolio/wedding_reception.jpg", "Wedding reception dance floor");
        createPortfolioItem(venueVendor1, "/images/portfolio/grand_ballroom.jpg", "Grand ballroom setup");
        createPortfolioItem(floristVendor1, "/images/portfolio/bridal_bouquet.jpg", "Beautiful bridal bouquet arrangement");

        // Create comprehensive reviews (25+ reviews)
        createReview(photoVendor1, customer1, 5, "Absolutely amazing photographer! John captured every precious moment of our wedding day. The photos are stunning and we couldn't be happier!");
        createReview(photoVendor1, customer2, 5, "Professional, creative, and so easy to work with. Our engagement photos turned out perfect!");
        createReview(photoVendor1, customer3, 4, "Great quality work and very responsive. Highly recommend for any special event.");
        
        createReview(photoVendor2, customer4, 5, "Mike's artistic eye is incredible. Our family portraits are absolutely beautiful and we treasure them.");
        createReview(photoVendor2, customer5, 4, "Professional service and great attention to detail. Very satisfied with the results.");
        
        createReview(cateringVendor1, customer1, 5, "The food was exceptional and the service was flawless. Our guests are still talking about how delicious everything was!");
        createReview(cateringVendor1, customer6, 5, "Jane and her team went above and beyond. The presentation was beautiful and the taste was even better!");
        createReview(cateringVendor1, customer7, 4, "Great catering service for our corporate event. Professional and accommodating to dietary restrictions.");
        
        createReview(cateringVendor2, customer8, 5, "Outstanding cocktail party catering. The appetizers were creative and delicious. Will definitely book again!");
        createReview(cateringVendor2, customer9, 4, "Excellent service and quality. The staff was professional and the food was fresh and tasty.");
        
        createReview(djVendor1, customer10, 5, "David kept our wedding reception alive all night! Great music selection and perfect timing. Highly recommended!");
        createReview(djVendor1, customer11, 5, "Professional DJ service that really made our event special. Great equipment and music variety.");
        
        createReview(decorVendor1, customer12, 5, "Emma's decorations transformed our venue into something magical. Absolutely beautiful work!");
        createReview(decorVendor1, customer13, 4, "Creative and professional. The birthday party decorations were perfect and within budget.");
        
        createReview(djVendor2, customer14, 4, "Great entertainment for our anniversary party. Ryan was professional and played all our favorite songs.");
        createReview(decorVendor2, customer15, 5, "Ashley's event design was stunning. She understood our vision perfectly and executed it flawlessly.");
        
        createReview(venueVendor1, customer1, 5, "The Grand Ballroom is absolutely gorgeous. Perfect for our wedding with excellent service from the staff.");
        createReview(venueVendor2, customer2, 4, "Beautiful garden venue with great outdoor space. Perfect for our ceremony and reception.");
        
        createReview(floristVendor1, customer3, 5, "Maria created the most beautiful floral arrangements. The bridal bouquet was exactly what I dreamed of!");
        createReview(floristVendor2, customer4, 4, "Professional florist with creative designs. The centerpieces were elegant and fresh.");
        
        createReview(photoVendor3, customer5, 4, "Chris did a great job with our corporate headshots. Professional quality and quick turnaround.");
        createReview(cateringVendor3, customer6, 5, "Lisa's authentic cuisine was the highlight of our event. Amazing flavors and presentation!");
        createReview(djVendor3, customer7, 4, "Michael provided great entertainment for our company party. Good music and professional setup.");
        createReview(decorVendor3, customer8, 5, "Amanda's luxury styling was perfect for our gala. Elegant and sophisticated design.");
        createReview(photoVendor4, customer9, 4, "James captured our corporate event beautifully. Professional documentation and great photos.");

        System.out.println("Database initialization completed successfully with comprehensive sample data!");
        System.out.println("Created:");
        System.out.println("- 6 Service Categories");
        System.out.println("- 40 Users (5 planners, 20 vendors, 15 customers)");
        System.out.println("- 20 Vendor Profiles");
        System.out.println("- 40+ Services across all categories");
        System.out.println("- 25 Events of various types");
        System.out.println("- 10+ Guests across multiple events");
        System.out.println("- 10+ Vendor Bookings");
        System.out.println("- 13 Portfolio Items");
        System.out.println("- 25+ Reviews with ratings");
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
    */
}