
package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyPointDTO;
import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyStatsDTO;
import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyTierDTO;
import com.hoangthanhhong.badminton.entity.*;
import com.hoangthanhhong.badminton.enums.PointTransactionType;
import com.hoangthanhhong.badminton.exception.BadRequestException;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.LoyaltyMapper;
import com.hoangthanhhong.badminton.repository.*;
import com.hoangthanhhong.badminton.service.LoyaltyService;
import com.hoangthanhhong.badminton.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LoyaltyServiceImpl implements LoyaltyService {

    private final LoyaltyPointRepository loyaltyPointRepository;
    private final LoyaltyTierRepository loyaltyTierRepository;
    private final UserRepository userRepository;
    private final UserLoyaltyStatsRepository userLoyaltyStatsRepository;
    private final BookingRepository bookingRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final LoyaltyMapper loyaltyMapper;
    private final NotificationService notificationService;

    // ===== POINTS MANAGEMENT =====

    @Override
    public LoyaltyPointDTO earnPoints(Long userId, Integer points, PointTransactionType type,
            String description, String relatedEntityType, Long relatedEntityId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get or create loyalty stats
        UserLoyaltyStats stats = userLoyaltyStatsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserLoyaltyStats newStats = UserLoyaltyStats.builder()
                            .user(user)
                            .currentBalance(0)
                            .totalPointsEarned(0)
                            .totalPointsSpent(0)
                            .lifetimePoints(0)
                            .build();
                    return userLoyaltyStatsRepository.save(newStats);
                });

        // Calculate expiry date (1 year from now)
        LocalDateTime expiresAt = LocalDateTime.now().plusYears(1);

        // Create loyalty point transaction
        LoyaltyPoint loyaltyPoint = LoyaltyPoint.builder()
                .user(user)
                .transactionType(type)
                .points(points)
                .balanceAfter(stats.getCurrentBalance() + points)
                .description(description)
                .relatedEntityType(relatedEntityType)
                .relatedEntityId(relatedEntityId)
                .expiresAt(expiresAt)
                .isExpired(false)
                .isReversed(false)
                .build();

        loyaltyPoint = loyaltyPointRepository.save(loyaltyPoint);

        // Update stats
        stats.earnPoints(points);
        stats.setLastEarnedAt(LocalDateTime.now());
        userLoyaltyStatsRepository.save(stats);

        // Check and upgrade tier
        checkAndUpgradeTier(userId);

        // Send notification
        notificationService.sendNotification(
                userId,
                com.hoangthanhhong.badminton.enums.NotificationType.POINTS_EARNED,
                "Points Earned",
                String.format("You earned %d points! %s", points, description),
                java.util.Map.of("points", points, "balance", stats.getCurrentBalance()));

        log.info("User {} earned {} points. New balance: {}", userId, points, stats.getCurrentBalance());

        return loyaltyMapper.toPointDTO(loyaltyPoint);
    }

    @Override
    public LoyaltyPointDTO spendPoints(Long userId, Integer points, PointTransactionType type, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserLoyaltyStats stats = userLoyaltyStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty stats not found"));

        // Check if user has enough points
        if (stats.getCurrentBalance() < points) {
            throw new BadRequestException("Insufficient points balance");
        }

        // Create loyalty point transaction
        LoyaltyPoint loyaltyPoint = LoyaltyPoint.builder()
                .user(user)
                .transactionType(type)
                .points(-points) // Negative for spending
                .balanceAfter(stats.getCurrentBalance() - points)
                .description(description)
                .isExpired(false)
                .isReversed(false)
                .build();

        loyaltyPoint = loyaltyPointRepository.save(loyaltyPoint);

        // Update stats
        stats.spendPoints(points);
        stats.setLastSpentAt(LocalDateTime.now());
        userLoyaltyStatsRepository.save(stats);

        // Send notification
        notificationService.sendNotification(
                userId,
                com.hoangthanhhong.badminton.enums.NotificationType.POINTS_REDEEMED,
                "Points Redeemed",
                String.format("You redeemed %d points. %s", points, description),
                java.util.Map.of("points", points, "balance", stats.getCurrentBalance()));

        log.info("User {} spent {} points. New balance: {}", userId, points, stats.getCurrentBalance());

        return loyaltyMapper.toPointDTO(loyaltyPoint);
    }

    @Override
    public void reverseTransaction(Long transactionId, Long reversedBy, String reason) {
        LoyaltyPoint transaction = loyaltyPointRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transaction.getIsReversed()) {
            throw new BadRequestException("Transaction already reversed");
        }

        User user = transaction.getUser();
        UserLoyaltyStats stats = userLoyaltyStatsRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty stats not found"));

        // Reverse the points
        Integer pointsToReverse = -transaction.getPoints(); // Negate the original amount

        // Create reversal transaction
        LoyaltyPoint reversal = LoyaltyPoint.builder()
                .user(user)
                .transactionType(PointTransactionType.ADJUSTMENT)
                .points(pointsToReverse)
                .balanceAfter(stats.getCurrentBalance() + pointsToReverse)
                .description("Reversal: " + reason)
                .reference("REVERSAL_" + transactionId)
                .isExpired(false)
                .isReversed(false)
                .build();

        loyaltyPointRepository.save(reversal);

        // Mark original as reversed
        transaction.setIsReversed(true);
        transaction.setReversedAt(LocalDateTime.now());
        transaction.setReversedBy(reversedBy);
        transaction.setReversalReason(reason);
        loyaltyPointRepository.save(transaction);

        // Update stats
        if (transaction.getPoints() > 0) {
            stats.setTotalPointsEarned(stats.getTotalPointsEarned() - transaction.getPoints());
        } else {
            stats.setTotalPointsSpent(stats.getTotalPointsSpent() + transaction.getPoints());
        }
        stats.setCurrentBalance(stats.getCurrentBalance() + pointsToReverse);
        userLoyaltyStatsRepository.save(stats);

        log.info("Reversed transaction {} for user {}. Reason: {}", transactionId, user.getId(), reason);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getUserPointBalance(Long userId) {
        return userLoyaltyStatsRepository.findByUserId(userId)
                .map(UserLoyaltyStats::getCurrentBalance)
                .orElse(0);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoyaltyPointDTO> getUserPointHistory(Long userId, Pageable pageable) {
        Page<LoyaltyPoint> points = loyaltyPointRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return points.map(loyaltyMapper::toPointDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public LoyaltyStatsDTO getUserLoyaltyStats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserLoyaltyStats stats = userLoyaltyStatsRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserLoyaltyStats newStats = UserLoyaltyStats.builder()
                            .user(user)
                            .currentBalance(0)
                            .totalPointsEarned(0)
                            .totalPointsSpent(0)
                            .lifetimePoints(0)
                            .build();
                    return userLoyaltyStatsRepository.save(newStats);
                });

        // Get current tier
        LoyaltyTier currentTier = getCurrentTierForPoints(stats.getLifetimePoints());

        // Get next tier
        LoyaltyTier nextTier = getNextTierForPoints(stats.getLifetimePoints());

        return loyaltyMapper.toStatsDTO(stats, currentTier, nextTier);
    }

    // ===== EXPIRATION =====

    @Override
    public void processExpiringPoints() {
        LocalDateTime now = LocalDateTime.now();
        List<LoyaltyPoint> expiringPoints = loyaltyPointRepository.findExpiringPoints(now);

        for (LoyaltyPoint point : expiringPoints) {
            if (!point.getIsExpired() && point.getPoints() > 0) {
                // Mark as expired
                point.setIsExpired(true);
                loyaltyPointRepository.save(point);

                // Update user stats
                UserLoyaltyStats stats = userLoyaltyStatsRepository.findByUserId(point.getUser().getId())
                        .orElse(null);

                if (stats != null) {
                    stats.setCurrentBalance(stats.getCurrentBalance() - point.getPoints());
                    userLoyaltyStatsRepository.save(stats);
                }

                // Send notification
                notificationService.sendNotification(
                        point.getUser().getId(),
                        com.hoangthanhhong.badminton.enums.NotificationType.POINTS_EXPIRED,
                        "Points Expired",
                        String.format("%d points have expired", point.getPoints()),
                        java.util.Map.of("points", point.getPoints()));

                log.info("Expired {} points for user {}", point.getPoints(), point.getUser().getId());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoyaltyPointDTO> getExpiringPoints(Long userId, Integer daysUntilExpiry) {
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(daysUntilExpiry);
        List<LoyaltyPoint> points = loyaltyPointRepository.findExpiringPointsForUser(userId, expiryDate);

        return points.stream()
                .map(loyaltyMapper::toPointDTO)
                .collect(Collectors.toList());
    }

    // ===== TIERS =====

    @Override
    @Transactional(readOnly = true)
    public List<LoyaltyTierDTO> getAllTiers() {
        List<LoyaltyTier> tiers = loyaltyTierRepository.findAllByOrderByLevelAsc();
        return tiers.stream()
                .map(loyaltyMapper::toTierDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public LoyaltyTierDTO getUserCurrentTier(Long userId) {
        UserLoyaltyStats stats = userLoyaltyStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty stats not found"));

        LoyaltyTier tier = getCurrentTierForPoints(stats.getLifetimePoints());
        return tier != null ? loyaltyMapper.toTierDTO(tier) : null;
    }

    @Override
    @Transactional(readOnly = true)
    public LoyaltyTierDTO getNextTier(Long userId) {
        UserLoyaltyStats stats = userLoyaltyStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty stats not found"));

        LoyaltyTier tier = getNextTierForPoints(stats.getLifetimePoints());
        return tier != null ? loyaltyMapper.toTierDTO(tier) : null;
    }

    @Override
    public void checkAndUpgradeTier(Long userId) {
        UserLoyaltyStats stats = userLoyaltyStatsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Loyalty stats not found"));

        LoyaltyTier currentTier = getCurrentTierForPoints(stats.getLifetimePoints());

        if (currentTier != null) {
            // Check if tier changed
            String currentTierName = stats.getCurrentTier();

            if (currentTierName == null || !currentTierName.equals(currentTier.getName())) {
                // Tier upgraded!
                stats.setCurrentTier(currentTier.getName());
                userLoyaltyStatsRepository.save(stats);

                // Send notification
                notificationService.sendNotification(
                        userId,
                        com.hoangthanhhong.badminton.enums.NotificationType.TIER_UPGRADED,
                        "Tier Upgraded!",
                        String.format("Congratulations! You've been upgraded to %s tier", currentTier.getName()),
                        java.util.Map.of("tier", currentTier.getName()));

                log.info("User {} upgraded to tier {}", userId, currentTier.getName());
            }
        }
    }

    // ===== REWARDS =====

    @Override
    public void rewardBookingCompletion(Long bookingId, Long userId, Double amount) {
        // Calculate points (1 point per 1000 VND)
        Integer points = (int) (amount / 1000);

        if (points > 0) {
            earnPoints(userId, points, PointTransactionType.BOOKING_COMPLETED,
                    "Booking completed", "BOOKING", bookingId);
        }
    }

    @Override
    public void rewardOrderCompletion(Long orderId, Long userId, Double amount) {
        // Calculate points (1 point per 1000 VND)
        Integer points = (int) (amount / 1000);

        if (points > 0) {
            earnPoints(userId, points, PointTransactionType.ORDER_COMPLETED,
                    "Order completed", "ORDER", orderId);
        }
    }

    @Override
    public void rewardReviewSubmission(Long reviewId, Long userId) {
        earnPoints(userId, 50, PointTransactionType.REVIEW_SUBMITTED,
                "Review submitted", "REVIEW", reviewId);
    }

    @Override
    public void rewardReferral(Long referrerId, Long referredUserId) {
        earnPoints(referrerId, 500, PointTransactionType.REFERRAL,
                "Referral bonus", "USER", referredUserId);
    }

    @Override
    public void rewardBirthday(Long userId) {
        earnPoints(userId, 1000, PointTransactionType.BIRTHDAY_BONUS,
                "Happy Birthday! Here's a special gift for you", null, null);
    }

    // ===== HELPER METHODS =====

    private LoyaltyTier getCurrentTierForPoints(Integer lifetimePoints) {
        return loyaltyTierRepository.findTierForPoints(lifetimePoints != null ? lifetimePoints : 0)
                .orElse(null);
    }

    private LoyaltyTier getNextTierForPoints(Integer lifetimePoints) {
        List<LoyaltyTier> tiers = loyaltyTierRepository.findAllByOrderByLevelAsc();

        for (LoyaltyTier tier : tiers) {
            if (tier.getMinPoints() > (lifetimePoints != null ? lifetimePoints : 0)) {
                return tier;
            }
        }

        return null; // Already at highest tier
    }
}
