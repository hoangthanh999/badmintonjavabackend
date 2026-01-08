
package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyPointDTO;
import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyStatsDTO;
import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyTierDTO;
import com.hoangthanhhong.badminton.entity.LoyaltyPoint;
import com.hoangthanhhong.badminton.entity.LoyaltyTier;
import com.hoangthanhhong.badminton.entity.UserLoyaltyStats;
import org.springframework.stereotype.Component;

@Component
public class LoyaltyMapper {

    // ===== LOYALTY POINT =====

    public LoyaltyPointDTO toPointDTO(LoyaltyPoint point) {
        if (point == null)
            return null;

        return LoyaltyPointDTO.builder()
                .id(point.getId())
                .userId(point.getUser().getId())
                .userName(point.getUser().getName())
                .transactionType(point.getTransactionType())
                .points(point.getPoints())
                .balanceAfter(point.getBalanceAfter())
                .description(point.getDescription())
                .reference(point.getReference())
                .relatedEntityType(point.getRelatedEntityType())
                .relatedEntityId(point.getRelatedEntityId())
                .expiresAt(point.getExpiresAt())
                .isExpired(point.getIsExpired())
                .isReversed(point.getIsReversed())
                .createdAt(point.getCreatedAt())
                .build();
    }

    // ===== LOYALTY TIER =====

    public LoyaltyTierDTO toTierDTO(LoyaltyTier tier) {
        if (tier == null)
            return null;

        return LoyaltyTierDTO.builder()
                .id(tier.getId())
                .name(tier.getName())
                .description(tier.getDescription())
                .level(tier.getLevel())
                .minPoints(tier.getMinPoints())
                .maxPoints(tier.getMaxPoints())
                .color(tier.getColor())
                .icon(tier.getIcon())
                .badgeUrl(tier.getBadgeUrl())
                .discountPercentage(tier.getDiscountPercentage())
                .pointsMultiplier(tier.getPointsMultiplier())
                .freeBookingsPerMonth(tier.getFreeBookingsPerMonth())
                .priorityBooking(tier.getPriorityBooking())
                .freeShipping(tier.getFreeShipping())
                .birthdayBonusPoints(tier.getBirthdayBonusPoints())
                .benefits(tier.getBenefits())
                .build();
    }

    public LoyaltyTierDTO toTierDTOWithStats(LoyaltyTier tier, Integer totalUsers, Boolean isCurrent) {
        LoyaltyTierDTO dto = toTierDTO(tier);
        dto.setTotalUsers(totalUsers);
        dto.setIsCurrent(isCurrent);
        return dto;
    }

    // ===== LOYALTY STATS =====

    public LoyaltyStatsDTO toStatsDTO(UserLoyaltyStats stats, LoyaltyTier currentTier, LoyaltyTier nextTier) {
        if (stats == null)
            return null;

        Integer pointsToNextTier = null;
        if (nextTier != null && nextTier.getMinPoints() != null) {
            pointsToNextTier = nextTier.getMinPoints() - stats.getCurrentBalance();
            if (pointsToNextTier < 0)
                pointsToNextTier = 0;
        }

        return LoyaltyStatsDTO.builder()
                .userId(stats.getUser().getId())
                .userName(stats.getUser().getName())
                .userEmail(stats.getUser().getEmail())
                .currentBalance(stats.getCurrentBalance())
                .totalPointsEarned(stats.getTotalPointsEarned())
                .totalPointsSpent(stats.getTotalPointsSpent())
                .lifetimePoints(stats.getLifetimePoints())
                .pointsExpiringSoon(stats.getPointsExpiringSoon())
                .nextExpiryDate(stats.getNextExpiryDate())
                .totalBookings(stats.getTotalBookings())
                .totalOrders(stats.getTotalOrders())
                .totalSpent(stats.getTotalSpent())
                .totalReferrals(stats.getTotalReferrals())
                .successfulReferrals(stats.getSuccessfulReferrals())
                .referralSuccessRate(stats.getReferralSuccessRate())
                .currentTier(currentTier != null ? currentTier.getName() : null)
                .nextTier(nextTier != null ? nextTier.getName() : null)
                .pointsToNextTier(pointsToNextTier)
                .discountPercentage(currentTier != null ? currentTier.getDiscountPercentage() : null)
                .pointsMultiplier(currentTier != null ? currentTier.getPointsMultiplier() : null)
                .lastEarnedAt(stats.getLastEarnedAt())
                .lastSpentAt(stats.getLastSpentAt())
                .build();
    }
}
