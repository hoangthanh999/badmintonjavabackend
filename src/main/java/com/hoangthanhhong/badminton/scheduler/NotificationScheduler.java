package com.hoangthanhhong.badminton.scheduler;

import com.hoangthanhhong.badminton.entity.Notification;
import com.hoangthanhhong.badminton.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationScheduler {

    private final NotificationRepository notificationRepository;

    /**
     * Process pending email notifications every 5 minutes
     */
    @Scheduled(fixedDelay = 300000) // 5 minutes
    @Transactional
    public void processPendingEmailNotifications() {
        log.debug("Processing pending email notifications...");

        List<Notification> notifications = notificationRepository.findPendingEmailNotifications(
                PageRequest.of(0, 100));

        for (Notification notification : notifications) {
            try {

                notification.markEmailSent();
                notificationRepository.save(notification);

            } catch (Exception e) {
                log.error("Failed to send email notification: {}", notification.getId(), e);
            }
        }

        if (!notifications.isEmpty()) {
            log.info("Processed {} email notifications", notifications.size());
        }
    }

    /**
     * Process pending SMS notifications every 2 minutes
     */
    @Scheduled(fixedDelay = 120000) // 2 minutes
    @Transactional
    public void processPendingSmsNotifications() {
        log.debug("Processing pending SMS notifications...");

        List<Notification> notifications = notificationRepository.findPendingSmsNotifications(
                PageRequest.of(0, 50));

        for (Notification notification : notifications) {
            try {

                notification.markSmsSent();
                notificationRepository.save(notification);

            } catch (Exception e) {
                log.error("Failed to send SMS notification: {}", notification.getId(), e);
            }
        }

        if (!notifications.isEmpty()) {
            log.info("Processed {} SMS notifications", notifications.size());
        }
    }

    /**
     * Process pending push notifications every minute
     */
    @Scheduled(fixedDelay = 60000) // 1 minute
    @Transactional
    public void processPendingPushNotifications() {
        log.debug("Processing pending push notifications...");

        List<Notification> notifications = notificationRepository.findPendingPushNotifications(
                PageRequest.of(0, 200));

        for (Notification notification : notifications) {
            try {

                notification.markPushSent();
                notification.markAsSent();
                notificationRepository.save(notification);

            } catch (Exception e) {
                log.error("Failed to send push notification: {}", notification.getId(), e);
            }
        }

        if (!notifications.isEmpty()) {
            log.info("Processed {} push notifications", notifications.size());
        }
    }

    /**
     * Delete expired notifications every day at 3:00 AM
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void deleteExpiredNotifications() {
        log.info("Deleting expired notifications...");

        notificationRepository.deleteExpiredNotifications(LocalDateTime.now());

        log.info("Expired notifications deleted");
    }
}
