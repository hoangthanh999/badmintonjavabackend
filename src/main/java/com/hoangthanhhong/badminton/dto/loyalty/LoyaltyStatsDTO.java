// File: LoyaltyStatsDTO.java (CẬP NHẬT)
package com.hoangthanhhong.badminton.dto.loyalty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate; // ✅ ĐỔI từ LocalDateTime
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyStatsDTO {
    private Long userId;
    private String userName;
    private String userEmail;

    // Points
    private Integer currentBalance;
    private Integer totalPointsEarned;
    private Integer totalPointsSpent;
    private Integer lifetimePoints;
    private Integer pointsExpiringSoon;
    private LocalDate nextExpiryDate; // ✅ ĐỔI từ LocalDateTime sang LocalDate

    // Activity
    private Integer totalBookings;
    private Integer totalOrders;
    private Double totalSpent;
    private Integer totalReferrals;
    private Integer successfulReferrals;
    private Double referralSuccessRate;

    // Tier
    private String currentTier;
    private String nextTier;
    private Integer pointsToNextTier;
    private Double discountPercentage;
    private Double pointsMultiplier;

    // Timestamps
    private LocalDateTime lastEarnedAt;
    private LocalDateTime lastSpentAt;
}
