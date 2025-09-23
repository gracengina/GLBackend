package com.evently.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * PortfolioItem entity converted from Django PortfolioItem model.
 * Represents a portfolio item (image/file) for a vendor's work showcase.
 * 
 * Maps to Django table: vendors_portfolioitem
 */
@Entity
@Table(name = "vendors_portfolioitem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PortfolioItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @NotNull
    private VendorProfile vendor;
    
    @Column(name = "image", length = 255)
    private String image; // File path/URL
    
    @Column(name = "description", length = 255)
    @Size(max = 255)
    private String description;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean hasImage() {
        return image != null && !image.trim().isEmpty();
    }
    
    public String getVendorBusinessName() {
        return vendor != null ? vendor.getBusinessName() : "Unknown Vendor";
    }
    
    public String getDisplayDescription() {
        if (description != null && !description.trim().isEmpty()) {
            return description.trim();
        }
        return "Portfolio item for " + getVendorBusinessName();
    }
    
    @Override
    public String toString() {
        return String.format("Portfolio Item for %s", getVendorBusinessName());
    }
}