package com.evently.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "events_vendorbooking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class VendorBooking {
    
    public enum BookingStatus {
        PENDING("pending"),
        CONFIRMED("confirmed"),
        CANCELLED("cancelled"),
        COMPLETED("completed");
        
        private final String value;
        
        BookingStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @NotNull
    private Event event;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    @NotNull
    private VendorProfile vendor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @NotNull
    private Service service;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private BookingStatus status = BookingStatus.PENDING;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    
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
    
    
    public boolean isPending() {
        return BookingStatus.PENDING.equals(status);
    }
    
    public boolean isConfirmed() {
        return BookingStatus.CONFIRMED.equals(status);
    }
    
    public boolean isCancelled() {
        return BookingStatus.CANCELLED.equals(status);
    }
    
    public boolean isCompleted() {
        return BookingStatus.COMPLETED.equals(status);
    }
    
    @Override
    public String toString() {
        return String.format("%s - %s for %s (%s)", 
            vendor != null ? vendor.getBusinessName() : "Unknown Vendor",
            service != null ? service.getTitle() : "Unknown Service",
            event != null ? event.getTitle() : "Unknown Event", 
            status.getValue());
    }
}
