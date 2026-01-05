
package com.hoangthanhhong.badminton.dto.loyalty;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyStatsDTO {
    private Long userId;
    private String userName;
    private Integer currentBalance;
    private Integer totalPointsEarned;
    private Integer totalPointsSpent;
    private Integer lifetimePoints;
    private Integer pointsExpiringSoon;
    private LocalDateTime nextExpiryDate;
    private Integer totalBookings;
    private Integer totalOrders;
    private Double totalSpent;
    private String currentTier;
    private String nextTier;
    private Integer pointsToNextTier;
}
