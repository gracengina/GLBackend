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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "events_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"guests", "vendorBookings"}) 
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planner_id", nullable = false)
    @NotNull
    private User planner;
    
    @Column(name = "title", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "date", nullable = false)
    @NotNull
    private LocalDateTime date;
    
    @Column(name = "location", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String location;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<Guest> guests = new ArrayList<>();
    
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
    private List<VendorBooking> vendorBookings = new ArrayList<>();
    
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
    public int getGuestCount() {
        return guests != null ? guests.size() : 0;
    }
    
    public int getVendorCount() {
        return vendorBookings != null ? vendorBookings.size() : 0;
    }
    
    public String getFormattedDate() {
        return date != null ? date.toString() : "";
    }
}
