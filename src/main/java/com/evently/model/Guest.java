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
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Guest entity converted from Django Guest model.
 * Represents a guest invited to an event with RSVP status.
 * 
 * Maps to Django table: events_guest
 */
@Entity
@Table(name = "events_guest")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Guest {
    
    public enum RsvpStatus {
        INVITED("invited"),
        ATTENDING("attending"),
        DECLINED("declined"),
        WAITLIST("waitlist");
        
        private final String value;
        
        RsvpStatus(String value) {
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
    @JoinColumn(name = "user_id")
    private User user; // Optional - guest might not be a registered user
    
    @Column(name = "name", length = 255)
    @Size(max = 255)
    private String name;
    
    @Column(name = "email", nullable = false, length = 254)
    @NotBlank
    @Email
    @Size(max = 254)
    private String email;
    
    @Column(name = "phone", length = 20)
    @Size(max = 20)
    private String phone;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "rsvp_status", nullable = false, length = 10)
    private RsvpStatus rsvpStatus = RsvpStatus.INVITED;
    
    @Column(name = "dietary_restrictions", columnDefinition = "TEXT")
    private String dietaryRestrictions;
    
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
    public String getDisplayName() {
        if (name != null && !name.trim().isEmpty()) {
            return name.trim();
        }
        if (user != null) {
            return user.getFullName();
        }
        return email;
    }
    
    public boolean isRegisteredUser() {
        return user != null;
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) for %s", 
            getDisplayName(), 
            rsvpStatus.getValue(), 
            event != null ? event.getTitle() : "Unknown Event");
    }
}