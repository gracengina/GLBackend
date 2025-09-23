package com.evently.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.evently.model.PortfolioItem;
import com.evently.dto.vendor.PortfolioItemDTO;

/**
 * MapStruct mapper for PortfolioItem entity and DTOs.
 * Handles conversion between PortfolioItem entity and PortfolioItemDTO.
 */
@Mapper(componentModel = "spring")
public interface PortfolioItemMapper {
    
    /**
     * Convert PortfolioItem entity to PortfolioItemDTO.
     * Maps vendor information.
     */
    @Mapping(target = "vendorId", source = "vendor.id")
    @Mapping(target = "vendorBusinessName", source = "vendor.businessName")
    PortfolioItemDTO toPortfolioItemDTO(PortfolioItem portfolioItem);
    
    /**
     * Convert list of PortfolioItem entities to list of DTOs.
     */
    List<PortfolioItemDTO> toPortfolioItemDTOList(List<PortfolioItem> portfolioItems);
    
    /**
     * Convert PortfolioItemDTO to PortfolioItem entity.
     * Excludes computed fields and relationships.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vendor", ignore = true) // Will be set separately
    @Mapping(target = "createdAt", ignore = true)
    PortfolioItem toPortfolioItem(PortfolioItemDTO portfolioItemDTO);
}