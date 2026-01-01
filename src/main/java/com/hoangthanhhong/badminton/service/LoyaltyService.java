package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyPointDTO;
import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyStatsDTO;
import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyTierDTO;
import com.hoangthanhhong.badminton.enums.PointTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LoyaltyService {

    // Points management
    LoyaltyPointDTO earnPoints(Long userId, Integer points, PointTransactionType type, String description,
            String relatedEntityType, Long relatedEntityId);

    LoyaltyPointDTO spendPoints(Long userId, Integer points, PointTransactionType type, String description);

    void reverseTransaction(Long transactionId, Long reversedBy, String reason);

    // Get points
    Integer getUserPointBalance(Long userId);

    Page<LoyaltyPointDTO> getUserPointHistory(Long userId, Pageable pageable);

    LoyaltyStatsDTO getUserLoyaltyStats(Long userId);

    // Expiration
    void processExpiringPoints();

    List<LoyaltyPointDTO> getExpiringPoints(Long userId, Integer daysUntilExpiry);

    // Tiers
    List<LoyaltyTierDTO> getAllTiers();

    LoyaltyTierDTO getUserCurrentTier(Long userId);

    LoyaltyTierDTO getNextTier(Long userId);

    void checkAndUpgradeTier(Long userId);

    // Rewards
    void rewardBookingCompletion(Long bookingId, Long userId, Double amount);

    void rewardOrderCompletion(Long orderId, Long userId, Double amount);

    void rewardReviewSubmission(Long reviewId, Long userId);

    void rewardReferral(Long referrerId, Long referredUserId);

    void rewardBirthday(Long userId);
}
