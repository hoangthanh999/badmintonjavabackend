package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loyalty_tiers", indexes = {
        @Index(name = "idx_level", columnList = "level")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyTier extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, unique = true)
    private Integer level; // 1 = Bronze, 2 = Silver, 3 = Gold, 4 = Platinum

    @Column(name = "min_points", nullable = false)
    private Integer minPoints;

    @Column(name = "max_points")
    private Integer maxPoints;

    @Column(length = 50)
    private String color; // For UI display

    @Column(length = 100)
    private String icon;

    @Column(name = "badge_url", length = 500)
    private String badgeUrl;

    // === BENEFITS ===

    @Column(name = "discount_percentage")
    private Double discountPercentage;

    @Column(name = "points_multiplier")
    @Builder.Default
    private Double pointsMultiplier = 1.0;

    @Column(name = "free_bookings_per_month")
    private Integer freeBookingsPerMonth;

    @Column(name = "priority_booking")
    @Builder.Default
    private Boolean priorityBooking = false;

    @Column(name = "free_shipping")
    @Builder.Default
    private Boolean freeShipping = false;

    @Column(name = "birthday_bonus_points")
    private Integer birthdayBonusPoints;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits; // JSON array of benefits

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "loyaltyTier")
    @Builder.Default
    private List<User> users = new ArrayList<>();

    // === HELPER METHODS ===

    public boolean isInRange(Integer points) {
        if (points < minPoints)
            return false;
        if (maxPoints == null)
            return true;
        return points <= maxPoints;
    }

    public Integer getPointsToNextTier() {
        return maxPoints != null ? maxPoints - minPoints + 1 : null;
    }
}
