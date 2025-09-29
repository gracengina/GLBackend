package com.evently.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "vendors_vendorprofile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"services", "portfolioItems", "reviews"}) 
public class VendorProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @NotNull
    private User user;
    
    @Column(name = "business_name", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String businessName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "location", length = 255)
    @Size(max = 255)
    private String location;
    
    @Column(name = "contact_info", length = 255)
    @Size(max = 255)
    private String contactInfo;
    
    @Column(name = "profile_pic", length = 255)
    private String profilePic; 
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    
    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    private List<Service> services = new ArrayList<>();
    
    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    private List<PortfolioItem> portfolioItems = new ArrayList<>();
    
    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    private List<Review> reviews = new ArrayList<>();
    
 
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (isVerified == null) {
            isVerified = false;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    

    public boolean isVerified() {
        return Boolean.TRUE.equals(isVerified);
    }
    
    public String getDisplayName() {
        return businessName != null ? businessName : 
               (user != null ? user.getFullName() : "Unknown Vendor");
    }
    
    public int getServicesCount() {
        return services != null ? services.size() : 0;
    }
    
    public int getPortfolioCount() {
        return portfolioItems != null ? portfolioItems.size() : 0;
    }
    
    public int getReviewsCount() {
        return reviews != null ? reviews.size() : 0;
    }
}
