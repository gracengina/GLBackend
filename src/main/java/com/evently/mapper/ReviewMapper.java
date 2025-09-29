package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.evently.model.Review;
import com.evently.dto.vendor.ReviewDTO;


@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    
    @Mapping(target = "vendorId", source = "vendor.id")
    @Mapping(target = "vendorBusinessName", source = "vendor.businessName")
    @Mapping(target = "customerId", source = "user.id")
    @Mapping(target = "customerUsername", source = "user.username")
    @Mapping(target = "customerFullName", source = "user", qualifiedByName = "getCustomerFullName")
    ReviewDTO toReviewDTO(Review review);
    
    
    List<ReviewDTO> toReviewDTOList(List<Review> reviews);
    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true)
    @Mapping(target = "user", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "rating", target = "rating")
    @Mapping(source = "comment", target = "comment")
    Review toReview(ReviewDTO reviewDTO);
    
    @Named("getCustomerFullName")
    default String getCustomerFullName(com.evently.model.User customer) {
        return customer != null ? customer.getFullName() : null;
    }
}
