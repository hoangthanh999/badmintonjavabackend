// File: LoyaltyTierDTO.java
package com.hoangthanhhong.badminton.dto.loyalty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyTierDTO {
    private Long id;
    private String name;
    private String description;
    private Integer level;
    private Integer minPoints;
    private Integer maxPoints;
    private String color;
    private String icon;
    private String badgeUrl;

    // Benefits
    private Double discountPercentage;
    private Double pointsMultiplier;
    private Integer freeBookingsPerMonth;
    private Boolean priorityBooking;
    private Boolean freeShipping;
    private Integer birthdayBonusPoints;
    private String benefits; // JSON

    // Stats
    private Integer totalUsers;
    private Boolean isCurrent;
}
