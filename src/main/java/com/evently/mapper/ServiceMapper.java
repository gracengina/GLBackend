package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.evently.model.Service;
import com.evently.dto.vendor.ServiceDTO;

/**
 * MapStruct mapper for Service entity and DTOs.
 * Handles conversion between Service entity and ServiceDTO.
 */
@Mapper(componentModel = "spring")
public interface ServiceMapper {
    
    /**
     * Convert Service entity to ServiceDTO.
     * Maps vendor and category information.
     */
    @Mapping(target = "vendorId", source = "vendor.id")
    @Mapping(target = "vendorBusinessName", source = "vendor.businessName")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ServiceDTO toServiceDTO(Service service);
    
    /**
     * Convert list of Service entities to list of ServiceDTOs.
     */
    List<ServiceDTO> toServiceDTOList(List<Service> services);
    
    /**
     * Convert ServiceDTO to Service entity.
     * Excludes computed fields and relationships.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true) // Will be set separately
    @Mapping(target = "category", ignore = true) // Will be set separately
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Service toService(ServiceDTO serviceDTO);
}