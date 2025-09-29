package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.evently.model.VendorBooking;
import com.evently.dto.booking.VendorBookingCreateDTO;
import com.evently.dto.booking.VendorBookingDTO;


@Mapper(componentModel = "spring")
public interface VendorBookingMapper {
    
    
    @Mapping(target = "eventId", source = "event.id")
    @Mapping(target = "eventTitle", source = "event.title")
    @Mapping(target = "eventDate", source = "event.date")
    @Mapping(target = "eventLocation", source = "event.location")
    @Mapping(target = "vendorId", source = "vendor.id")
    @Mapping(target = "vendorBusinessName", source = "vendor.businessName")
    @Mapping(target = "serviceId", source = "service.id")
    @Mapping(target = "serviceTitle", source = "service.title")
    @Mapping(target = "servicePrice", expression = "java(getServicePriceString(vendorBooking.getService()))")
    VendorBookingDTO toVendorBookingDTO(VendorBooking vendorBooking);
  
    List<VendorBookingDTO> toVendorBookingDTOList(List<VendorBooking> vendorBookings);
    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", ignore = true) 
    @Mapping(target = "vendor", ignore = true) 
    @Mapping(target = "service", ignore = true) 
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    VendorBooking toVendorBooking(VendorBookingCreateDTO createDTO);
    
    
    default String getServicePriceString(com.evently.model.Service service) {
        return service != null && service.getPrice() != null ? 
                "$" + service.getPrice().toString() : null;
    }
}
