package com.hoangthanhhong.badminton.scheduler;

import com.hoangthanhhong.badminton.dto.loyalty.LoyaltyPointDTO; // ✅ Thêm import này
import com.hoangthanhhong.badminton.entity.User;
import com.hoangthanhhong.badminton.repository.LoyaltyPointRepository;
import com.hoangthanhhong.badminton.repository.UserRepository;
import com.hoangthanhhong.badminton.service.LoyaltyService;
import com.hoangthanhhong.badminton.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LoyaltyScheduler {

    private final LoyaltyPointRepository loyaltyPointRepository;
    private final UserRepository userRepository;
    private final LoyaltyService loyaltyService;
    private final NotificationService notificationService;

    /**
     * Process expiring points every day at 1:00 AM
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void processExpiringPoints() {
        log.info("Processing expiring points...");

        loyaltyService.processExpiringPoints();

        log.info("Expiring points processed");
    }

    /**
     * Send expiring points reminders every day at 9:00 AM
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void sendExpiringPointsReminders() {
        log.info("Sending expiring points reminders...");

        List<User> users = userRepository.findAll();

        int sent = 0;
        for (User user : users) {
            // ✅ ĐỔI THÀNH LoyaltyPointDTO
            List<LoyaltyPointDTO> expiringPoints = loyaltyService.getExpiringPoints(user.getId(), 30);

            if (!expiringPoints.isEmpty()) {
                int totalExpiring = expiringPoints.stream()
                        .mapToInt(LoyaltyPointDTO::getPoints) // ✅ Đổi thành LoyaltyPointDTO::getPoints
                        .sum();

                notificationService.sendNotification(
                        user.getId(),
                        com.hoangthanhhong.badminton.enums.NotificationType.POINTS_EXPIRING,
                        "Points Expiring Soon",
                        String.format("You have %d points expiring in the next 30 days. Use them before they expire!",
                                totalExpiring),
                        java.util.Map.of("expiringPoints", totalExpiring));

                sent++;
            }
        }

        log.info("Sent {} expiring points reminders", sent);
    }

    /**
     * Send birthday bonus points every day at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void sendBirthdayBonusPoints() {
        log.info("Sending birthday bonus points...");

        LocalDate today = LocalDate.now();

        // Find users with birthday today
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getDateOfBirth() != null) // ✅ Đã có method này
                .filter(user -> {
                    LocalDate dob = user.getDateOfBirth();
                    return dob.getMonth() == today.getMonth() && dob.getDayOfMonth() == today.getDayOfMonth();
                })
                .toList();

        for (User user : users) {
            try {
                loyaltyService.rewardBirthday(user.getId());
                log.info("Sent birthday bonus to user: {}", user.getId());
            } catch (Exception e) {
                log.error("Failed to send birthday bonus to user: {}", user.getId(), e);
            }
        }

        log.info("Sent birthday bonus to {} users", users.size());
    }

    /**
     * Check and upgrade user tiers every day at 2:00 AM
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void checkAndUpgradeTiers() {
        log.info("Checking and upgrading user tiers...");

        List<User> users = userRepository.findAll();

        int upgraded = 0;
        for (User user : users) {
            try {
                loyaltyService.checkAndUpgradeTier(user.getId());
                upgraded++;
            } catch (Exception e) {
                log.error("Failed to check tier for user: {}", user.getId(), e);
            }
        }

        log.info("Checked tiers for {} users", upgraded);
    }
}
