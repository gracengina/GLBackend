package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.evently.dto.vendor.VendorProfileCreateUpdateDTO;
import com.evently.dto.vendor.VendorProfileDTO;
import com.evently.model.VendorProfile;

/**
 * MapStruct mapper for VendorProfile entity and DTOs.
 * Handles conversion between VendorProfile entity and various DTOs.
 */
@Mapper(componentModel = "spring", uses = {ServiceMapper.class, PortfolioItemMapper.class, ReviewMapper.class})
public interface VendorProfileMapper {
    
    /**
     * Convert VendorProfile entity to VendorProfileDTO.
     * Maps user information and computed fields.
     */
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "fullName", expression = "java(vendorProfile.getUser() != null ? vendorProfile.getUser().getFullName() : null)")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "servicesCount", expression = "java(vendorProfile.getServicesCount())")
    @Mapping(target = "portfolioCount", expression = "java(vendorProfile.getPortfolioCount())")
    @Mapping(target = "reviewsCount", expression = "java(vendorProfile.getReviewsCount())")
    @Mapping(target = "averageRating", expression = "java(getAverageRatingHelper(vendorProfile))")
    VendorProfileDTO toVendorProfileDTO(VendorProfile vendorProfile);
    
    /**
     * Convert list of VendorProfile entities to list of DTOs.
     */
    List<VendorProfileDTO> toVendorProfileDTOList(List<VendorProfile> vendorProfiles);
    
    /**
     * Convert VendorProfileCreateUpdateDTO to VendorProfile entity.
     * Excludes computed fields and relationships.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true) // Will be set from security context
    @Mapping(target = "profilePic", ignore = true) // Handled separately
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "portfolioItems", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    VendorProfile toVendorProfile(VendorProfileCreateUpdateDTO createUpdateDTO);
    
    /**
     * Update VendorProfile entity from VendorProfileCreateUpdateDTO.
     * Only updates modifiable fields.
     */
    // Update method for patching existing vendor profile
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "profilePic", ignore = true)
    @Mapping(target = "isVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "services", ignore = true)
    @Mapping(target = "portfolioItems", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    void updateVendorProfileFromDTO(VendorProfileCreateUpdateDTO updateDTO, @MappingTarget VendorProfile vendorProfile);
    
    /**
     * Helper method to calculate average rating.
     */
    default Double getAverageRatingHelper(VendorProfile vendorProfile) {
        if (vendorProfile.getReviews() == null || vendorProfile.getReviews().isEmpty()) {
            return null;
        }
        return vendorProfile.getReviews().stream()
                .mapToInt(review -> review.getRating())
                .average()
                .orElse(0.0);
    }
}