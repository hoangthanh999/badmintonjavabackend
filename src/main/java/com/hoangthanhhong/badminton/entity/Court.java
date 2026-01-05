package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.CourtStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courts", indexes = {
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_location", columnList = "location")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Court extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 200)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CourtStatus status = CourtStatus.AVAILABLE;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "base_price")
    private Double basePrice;

    @Column(name = "peak_hour_price")
    private Double peakHourPrice;

    @Column(name = "weekend_price")
    private Double weekendPrice;

    @Column(name = "court_number")
    private Integer courtNumber;

    @Column(name = "floor_type", length = 50)
    private String floorType; // Wood, Synthetic, etc.

    @Column(name = "lighting_quality", length = 50)
    private String lightingQuality;

    @Column(name = "has_air_conditioning")
    @Builder.Default
    private Boolean hasAirConditioning = false;

    @Column(name = "max_players")
    @Builder.Default
    private Integer maxPlayers = 4;

    @Column(name = "area_size")
    private Double areaSize; // m²

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Schedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CourtPrice> courtPrices = new ArrayList<>();

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Maintenance> maintenances = new ArrayList<>();

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "court", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Tournament> tournaments = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "court_amenities", joinColumns = @JoinColumn(name = "court_id"), inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    @Builder.Default
    private List<Amenity> amenities = new ArrayList<>();

    // === HELPER METHODS ===

    public void updateRating(Double newRating) {
        if (this.rating == null) {
            this.rating = newRating;
            this.totalReviews = 1;
        } else {
            this.rating = ((this.rating * this.totalReviews) + newRating) / (this.totalReviews + 1);
            this.totalReviews++;
        }
    }

    @Transient
    public String getCourtName() {
        return name;
    }

    public boolean isAvailable() {
        return status == CourtStatus.AVAILABLE;
    }
}
