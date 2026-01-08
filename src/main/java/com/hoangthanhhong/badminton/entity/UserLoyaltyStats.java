package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_loyalty_stats", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoyaltyStats extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "total_points_earned")
    @Builder.Default
    private Integer totalPointsEarned = 0;

    @Column(name = "total_points_spent")
    @Builder.Default
    private Integer totalPointsSpent = 0;

    @Column(name = "current_balance")
    @Builder.Default
    private Integer currentBalance = 0;

    @Column(name = "lifetime_points")
    @Builder.Default
    private Integer lifetimePoints = 0;

    @Column(name = "points_expiring_soon")
    @Builder.Default
    private Integer pointsExpiringSoon = 0;

    @Column(name = "next_expiry_date")
    private LocalDate nextExpiryDate;

    @Column(name = "total_bookings")
    @Builder.Default
    private Integer totalBookings = 0;

    @Column(name = "total_orders")
    @Builder.Default
    private Integer totalOrders = 0;

    @Column(name = "total_spent")
    @Builder.Default
    private Double totalSpent = 0.0;

    @Column(name = "total_referrals")
    @Builder.Default
    private Integer totalReferrals = 0;

    @Column(name = "successful_referrals")
    @Builder.Default
    private Integer successfulReferrals = 0;

    @Column(name = "last_earned_at")
    private java.time.LocalDateTime lastEarnedAt;

    @Column(name = "last_spent_at")
    private java.time.LocalDateTime lastSpentAt;
    @Column(name = "current_tier", length = 100)
    private String currentTier;
    // === HELPER METHODS ===

    public void earnPoints(Integer points) {
        this.currentBalance += points;
        this.totalPointsEarned += points;
        this.lifetimePoints += points;
        this.lastEarnedAt = java.time.LocalDateTime.now();
    }

    public void spendPoints(Integer points) {
        if (points > currentBalance) {
            throw new IllegalArgumentException("Insufficient points");
        }
        this.currentBalance -= points;
        this.totalPointsSpent += points;
        this.lastSpentAt = java.time.LocalDateTime.now();
    }

    public void incrementBookings() {
        this.totalBookings++;
    }

    public void incrementOrders() {
        this.totalOrders++;
    }

    public void addSpent(Double amount) {
        this.totalSpent += amount;
    }

    public void incrementReferrals() {
        this.totalReferrals++;
    }

    public void incrementSuccessfulReferrals() {
        this.successfulReferrals++;
    }

    public Double getReferralSuccessRate() {
        if (totalReferrals == 0)
            return 0.0;
        return (double) successfulReferrals / totalReferrals * 100;
    }

    public String getCurrentTier() {
        return currentTier;
    }

    public void setCurrentTier(String currentTier) {
        this.currentTier = currentTier;
    }
}
