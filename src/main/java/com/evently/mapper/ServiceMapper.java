package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.evently.model.Service;
import com.evently.dto.vendor.ServiceDTO;


@Mapper(componentModel = "spring")
public interface ServiceMapper {
    
    
    @Mapping(target = "vendorId", source = "vendor.id")
    @Mapping(target = "vendorBusinessName", source = "vendor.businessName")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ServiceDTO toServiceDTO(Service service);
    
    
    List<ServiceDTO> toServiceDTOList(List<Service> services);
   
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true) 
    @Mapping(target = "category", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Service toService(ServiceDTO serviceDTO);
}
