package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.evently.model.PortfolioItem;
import com.evently.dto.vendor.PortfolioItemDTO;


@Mapper(componentModel = "spring")
public interface PortfolioItemMapper {
    
    
    @Mapping(target = "vendorId", source = "vendor.id")
    @Mapping(target = "vendorBusinessName", source = "vendor.businessName")
    PortfolioItemDTO toPortfolioItemDTO(PortfolioItem portfolioItem);
    
    
    List<PortfolioItemDTO> toPortfolioItemDTOList(List<PortfolioItem> portfolioItems);
    
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true) 
    @Mapping(target = "createdAt", ignore = true)
    PortfolioItem toPortfolioItem(PortfolioItemDTO portfolioItemDTO);
}
