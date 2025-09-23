package com.evently.model;

import java.math.BigDecimal;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Service entity converted from Django Service model.
 * Represents a service offered by a vendor.
 * 
 * Maps to Django table: vendors_service
 */
@Entity
@Table(name = "vendors_service")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @NotNull
    private VendorProfile vendor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private ServiceCategory category;
    
    @Column(name = "title", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String title;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    @NotBlank
    private String description;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;
    
    @Column(name = "availability_notes", columnDefinition = "TEXT")
    private String availabilityNotes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper methods
    public String getFormattedPrice() {
        return price != null ? "$" + price.toString() : "Price not set";
    }
    
    public String getVendorBusinessName() {
        return vendor != null ? vendor.getBusinessName() : "Unknown Vendor";
    }
    
    public String getCategoryName() {
        return category != null ? category.getName() : "Uncategorized";
    }
    
    @Override
    public String toString() {
        return String.format("%s by %s", title, getVendorBusinessName());
    }
}