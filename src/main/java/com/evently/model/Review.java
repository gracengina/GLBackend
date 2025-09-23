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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Review entity converted from Django Review model.
 * Represents a customer review for a vendor.
 * 
 * Maps to Django table: vendors_review
 */
@Entity
@Table(name = "vendors_review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @NotNull
    private VendorProfile vendor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;
    
    @Column(name = "rating", nullable = false)
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating; // 1-5 stars
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Helper methods
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }
    
    public String getStarDisplay() {
        if (rating == null) return "No rating";
        return "★".repeat(rating) + "☆".repeat(5 - rating);
    }
    
    public String getVendorBusinessName() {
        return vendor != null ? vendor.getBusinessName() : "Unknown Vendor";
    }
    
    public String getReviewerName() {
        return user != null ? user.getFullName() : "Anonymous";
    }
    
    public String getShortComment(int maxLength) {
        if (!hasComment()) return "";
        String trimmed = comment.trim();
        if (trimmed.length() <= maxLength) return trimmed;
        return trimmed.substring(0, maxLength - 3) + "...";
    }
    
    @Override
    public String toString() {
        return String.format("Review for %s by %s (%s)", 
            getVendorBusinessName(), 
            getReviewerName(), 
            rating != null ? rating + " stars" : "No rating");
    }
}