package com.evently.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evently.model.PortfolioItem;
import com.evently.model.Review;
import com.evently.model.ServiceCategory;
import com.evently.model.User;
import com.evently.model.VendorProfile;
import com.evently.dto.vendor.PortfolioItemDTO;
import com.evently.dto.vendor.ReviewDTO;
import com.evently.dto.vendor.ServiceDTO;
import com.evently.dto.vendor.VendorProfileCreateUpdateDTO;
import com.evently.dto.vendor.VendorProfileDTO;
import com.evently.mapper.PortfolioItemMapper;
import com.evently.mapper.ReviewMapper;
import com.evently.mapper.ServiceMapper;
import com.evently.mapper.VendorProfileMapper;
import com.evently.repository.PortfolioItemRepository;
import com.evently.repository.ReviewRepository;
import com.evently.repository.ServiceCategoryRepository;
import com.evently.repository.ServiceRepository;
import com.evently.repository.UserRepository;
import com.evently.repository.VendorProfileRepository;

/**
 * Service layer for Vendor-related operations.
 * Handles vendor profile management, service offerings, and portfolio handling.
 */
@Service
@Transactional
public class VendorService {
    
    @Autowired
    private VendorProfileRepository vendorProfileRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private PortfolioItemRepository portfolioItemRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ServiceCategoryRepository serviceCategoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VendorProfileMapper vendorProfileMapper;
    
    @Autowired
    private ServiceMapper serviceMapper;
    
    @Autowired
    private PortfolioItemMapper portfolioItemMapper;
    
    @Autowired
    private ReviewMapper reviewMapper;
    
    // Vendor Profile Management
    
    /**
     * Create vendor profile.
     */
    public VendorProfileDTO createVendorProfile(VendorProfileCreateUpdateDTO createDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        if (!Boolean.TRUE.equals(user.getIsVendor())) {
            throw new IllegalArgumentException("User is not authorized to create vendor profile: " + userId);
        }
        
        // Check if vendor profile already exists
        if (vendorProfileRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("Vendor profile already exists for user: " + userId);
        }
        
        VendorProfile vendorProfile = vendorProfileMapper.toVendorProfile(createDTO);
        vendorProfile.setUser(user);
        
        VendorProfile savedProfile = vendorProfileRepository.save(vendorProfile);
        return vendorProfileMapper.toVendorProfileDTO(savedProfile);
    }
    
    /**
     * Get vendor profile by ID.
     */
    @Transactional(readOnly = true)
    public Optional<VendorProfileDTO> getVendorProfileById(Long id) {
        return vendorProfileRepository.findById(id)
                .map(vendorProfileMapper::toVendorProfileDTO);
    }
    
    /**
     * Get vendor profile by user ID.
     */
    @Transactional(readOnly = true)
    public Optional<VendorProfileDTO> getVendorProfileByUserId(Long userId) {
        return vendorProfileRepository.findByUserId(userId)
                .map(vendorProfileMapper::toVendorProfileDTO);
    }
    
    /**
     * Update vendor profile.
     */
    public VendorProfileDTO updateVendorProfile(Long profileId, VendorProfileCreateUpdateDTO updateDTO, Long userId) {
        VendorProfile vendorProfile = vendorProfileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor profile not found: " + profileId));
        
        // Check if the user owns this profile
        if (!vendorProfile.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this vendor profile");
        }
        
        vendorProfileMapper.updateVendorProfileFromDTO(updateDTO, vendorProfile);
        
        VendorProfile savedProfile = vendorProfileRepository.save(vendorProfile);
        return vendorProfileMapper.toVendorProfileDTO(savedProfile);
    }
    
    /**
     * Get all vendor profiles with pagination.
     */
    @Transactional(readOnly = true)
    public Page<VendorProfileDTO> getAllVendorProfiles(Pageable pageable) {
        Page<VendorProfile> vendors = vendorProfileRepository.findAll(pageable);
        return vendors.map(vendorProfileMapper::toVendorProfileDTO);
    }
    
    /**
     * Get verified vendor profiles.
     */
    @Transactional(readOnly = true)
    public List<VendorProfileDTO> getVerifiedVendorProfiles() {
        List<VendorProfile> vendors = vendorProfileRepository.findByIsVerified(true);
        return vendorProfileMapper.toVendorProfileDTOList(vendors);
    }
    
    /**
     * Search vendor profiles by business name or location.
     */
    @Transactional(readOnly = true)
    public List<VendorProfileDTO> searchVendorProfiles(String query) {
        List<VendorProfile> vendors = vendorProfileRepository.findByBusinessNameContainingIgnoreCaseOrLocationContainingIgnoreCase(query, query);
        return vendorProfileMapper.toVendorProfileDTOList(vendors);
    }
    
    /**
     * Get vendor profiles by location.
     */
    @Transactional(readOnly = true)
    public List<VendorProfileDTO> getVendorProfilesByLocation(String location) {
        List<VendorProfile> vendors = vendorProfileRepository.findByLocationContainingIgnoreCase(location);
        return vendorProfileMapper.toVendorProfileDTOList(vendors);
    }
    
    // Service Management
    
    /**
     * Add service to vendor profile.
     */
    public ServiceDTO addServiceToVendor(Long vendorId, ServiceDTO serviceDTO, Long userId) {
        VendorProfile vendorProfile = vendorProfileRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor profile not found: " + vendorId));
        
        // Check if the user owns this profile
        if (!vendorProfile.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to add services to this vendor profile");
        }
        
        com.evently.model.Service service = serviceMapper.toService(serviceDTO);
        service.setVendor(vendorProfile);
        
        // Set category if provided
        if (serviceDTO.getCategoryId() != null) {
            ServiceCategory category = serviceCategoryRepository.findById(serviceDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Service category not found: " + serviceDTO.getCategoryId()));
            service.setCategory(category);
        }
        
        com.evently.model.Service savedService = serviceRepository.save(service);
        return serviceMapper.toServiceDTO(savedService);
    }
    
    /**
     * Update service.
     */
    public ServiceDTO updateService(Long serviceId, ServiceDTO serviceDTO, Long userId) {
        com.evently.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));
        
        // Check if the user owns this service
        if (!service.getVendor().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this service");
        }
        
        // Update service fields
        service.setTitle(serviceDTO.getTitle());
        service.setDescription(serviceDTO.getDescription());
        service.setPrice(serviceDTO.getPrice());
        service.setAvailabilityNotes(serviceDTO.getAvailabilityNotes());
        
        // Update category if provided
        if (serviceDTO.getCategoryId() != null) {
            ServiceCategory category = serviceCategoryRepository.findById(serviceDTO.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("Service category not found: " + serviceDTO.getCategoryId()));
            service.setCategory(category);
        }
        
        com.evently.model.Service savedService = serviceRepository.save(service);
        return serviceMapper.toServiceDTO(savedService);
    }
    
    /**
     * Delete service.
     */
    public void deleteService(Long serviceId, Long userId) {
        com.evently.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found: " + serviceId));
        
        // Check if the user owns this service
        if (!service.getVendor().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this service");
        }
        
        serviceRepository.delete(service);
    }
    
    /**
     * Get services by vendor.
     */
    @Transactional(readOnly = true)
    public List<ServiceDTO> getServicesByVendor(Long vendorId) {
        List<com.evently.model.Service> services = serviceRepository.findByVendorId(vendorId);
        return serviceMapper.toServiceDTOList(services);
    }
    
    /**
     * Get services by category.
     */
    @Transactional(readOnly = true)
    public List<ServiceDTO> getServicesByCategory(Long categoryId) {
        List<com.evently.model.Service> services = serviceRepository.findByCategoryId(categoryId);
        return serviceMapper.toServiceDTOList(services);
    }
    
    // Portfolio Management
    
    /**
     * Add portfolio item to vendor.
     */
    public PortfolioItemDTO addPortfolioItem(Long vendorId, PortfolioItemDTO portfolioItemDTO, Long userId) {
        VendorProfile vendorProfile = vendorProfileRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor profile not found: " + vendorId));
        
        // Check if the user owns this profile
        if (!vendorProfile.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to add portfolio items to this vendor profile");
        }
        
        PortfolioItem portfolioItem = portfolioItemMapper.toPortfolioItem(portfolioItemDTO);
        portfolioItem.setVendor(vendorProfile);
        
        PortfolioItem savedItem = portfolioItemRepository.save(portfolioItem);
        return portfolioItemMapper.toPortfolioItemDTO(savedItem);
    }
    
    /**
     * Update portfolio item.
     */
    public PortfolioItemDTO updatePortfolioItem(Long itemId, PortfolioItemDTO portfolioItemDTO, Long userId) {
        PortfolioItem portfolioItem = portfolioItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio item not found: " + itemId));
        
        // Check if the user owns this portfolio item
        if (!portfolioItem.getVendor().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to update this portfolio item");
        }
        
        portfolioItem.setDescription(portfolioItemDTO.getDescription());
        portfolioItem.setImage(portfolioItemDTO.getImage());
        
        PortfolioItem savedItem = portfolioItemRepository.save(portfolioItem);
        return portfolioItemMapper.toPortfolioItemDTO(savedItem);
    }
    
    /**
     * Delete portfolio item.
     */
    public void deletePortfolioItem(Long itemId, Long userId) {
        PortfolioItem portfolioItem = portfolioItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Portfolio item not found: " + itemId));
        
        // Check if the user owns this portfolio item
        if (!portfolioItem.getVendor().getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not authorized to delete this portfolio item");
        }
        
        portfolioItemRepository.delete(portfolioItem);
    }
    
    /**
     * Get portfolio items by vendor.
     */
    @Transactional(readOnly = true)
    public List<PortfolioItemDTO> getPortfolioItemsByVendor(Long vendorId) {
        List<PortfolioItem> items = portfolioItemRepository.findByVendorId(vendorId);
        return portfolioItemMapper.toPortfolioItemDTOList(items);
    }
    
    // Review Management
    
    /**
     * Add review for vendor.
     */
    public ReviewDTO addReviewForVendor(Long vendorId, ReviewDTO reviewDTO, Long customerId) {
        VendorProfile vendorProfile = vendorProfileRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor profile not found: " + vendorId));
        
        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + customerId));
        
        // Check if customer has already reviewed this vendor
        if (reviewRepository.existsByVendorIdAndUserId(vendorId, customerId)) {
            throw new IllegalArgumentException("Customer has already reviewed this vendor");
        }
        
        Review review = reviewMapper.toReview(reviewDTO);
        review.setVendor(vendorProfile);
        review.setUser(customer);
        
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDTO(savedReview);
    }
    
    /**
     * Update review.
     */
    public ReviewDTO updateReview(Long reviewId, ReviewDTO reviewDTO, Long customerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        
        // Check if the customer owns this review
        if (!review.getUser().getId().equals(customerId)) {
            throw new IllegalArgumentException("User is not authorized to update this review");
        }
        
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewDTO(savedReview);
    }
    
    /**
     * Delete review.
     */
    public void deleteReview(Long reviewId, Long customerId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        
        // Check if the customer owns this review
        if (!review.getUser().getId().equals(customerId)) {
            throw new IllegalArgumentException("User is not authorized to delete this review");
        }
        
        reviewRepository.delete(review);
    }
    
    /**
     * Get reviews by vendor.
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByVendor(Long vendorId) {
        List<Review> reviews = reviewRepository.findByVendorId(vendorId);
        return reviewMapper.toReviewDTOList(reviews);
    }
    
    /**
     * Get reviews by vendor with rating filter.
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByVendorAndRating(Long vendorId, Integer rating) {
        List<Review> reviews = reviewRepository.findByVendorIdAndRating(vendorId, rating);
        return reviewMapper.toReviewDTOList(reviews);
    }
    
    /**
     * Get vendor statistics.
     */
    @Transactional(readOnly = true)
    public VendorStatsDTO getVendorStatistics(Long vendorId) {
        // Verify vendor exists
        vendorProfileRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor profile not found: " + vendorId));
        
        int totalServices = serviceRepository.countByVendorId(vendorId);
        int totalPortfolioItems = portfolioItemRepository.countByVendorId(vendorId);
        int totalReviews = reviewRepository.countByVendorId(vendorId);
        Double averageRating = reviewRepository.findAverageRatingByVendorId(vendorId);
        
        return new VendorStatsDTO(totalServices, totalPortfolioItems, totalReviews, averageRating);
    }
    
    /**
     * DTO for vendor statistics.
     */
    public static class VendorStatsDTO {
        private final int totalServices;
        private final int totalPortfolioItems;
        private final int totalReviews;
        private final Double averageRating;
        
        public VendorStatsDTO(int totalServices, int totalPortfolioItems, int totalReviews, Double averageRating) {
            this.totalServices = totalServices;
            this.totalPortfolioItems = totalPortfolioItems;
            this.totalReviews = totalReviews;
            this.averageRating = averageRating;
        }
        
        public int getTotalServices() { return totalServices; }
        public int getTotalPortfolioItems() { return totalPortfolioItems; }
        public int getTotalReviews() { return totalReviews; }
        public Double getAverageRating() { return averageRating; }
    }
}