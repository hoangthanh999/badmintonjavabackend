package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.notification.NotificationDTO;
import com.hoangthanhhong.badminton.dto.request.notification.CreateNotificationRequest;
import com.hoangthanhhong.badminton.entity.Notification;
import com.hoangthanhhong.badminton.entity.User;
import com.hoangthanhhong.badminton.enums.NotificationPriority;
import com.hoangthanhhong.badminton.enums.NotificationType;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.NotificationMapper;
import com.hoangthanhhong.badminton.repository.NotificationRepository;
import com.hoangthanhhong.badminton.repository.UserRepository;
import com.hoangthanhhong.badminton.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;

    @Override
    public NotificationDTO createNotification(CreateNotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        // Convert metadata map to JSON string
        String metadataJson = null;
        if (request.getMetadata() != null && !request.getMetadata().isEmpty()) {
            try {
                metadataJson = objectMapper.writeValueAsString(request.getMetadata());
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize metadata", e);
            }
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(request.getType())
                .priority(request.getPriority() != null ? request.getPriority() : NotificationPriority.NORMAL)
                .title(request.getTitle())
                .message(request.getMessage())
                .actionUrl(request.getActionUrl())
                .metadata(metadataJson)
                .sendEmail(request.getSendEmail() != null ? request.getSendEmail() : false)
                .sendSms(request.getSendSms() != null ? request.getSendSms() : false)
                .sendPush(request.getSendPush() != null ? request.getSendPush() : true)
                .isRead(false)
                .isSent(false)
                .emailSent(false)
                .smsSent(false)
                .pushSent(false)
                .build();

        notification = notificationRepository.save(notification);
        log.info("Notification created successfully with id: {}", notification.getId());

        return notificationMapper.toDTO(notification);
    }

    @Override
    public void deleteNotification(Long id) {
        log.info("Soft deleting notification with id: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        notification.setDeletedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        log.info("Notification soft deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationDTO getNotificationById(Long id) {
        log.info("Getting notification with id: {}", id);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (notification.getDeletedAt() != null) {
            throw new ResourceNotFoundException("Notification has been deleted");
        }

        return notificationMapper.toDTO(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable) {
        log.info("Getting notifications for user: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        return notifications.map(notificationMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        log.info("Getting unread notifications for user: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        List<Notification> notifications = notificationRepository.findUnreadByUserId(userId, LocalDateTime.now());
        return notifications.stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        log.info("Getting unread count for user: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return notificationRepository.countUnreadByUserId(userId, LocalDateTime.now());
    }

    @Override
    public void markAsRead(Long id, Long userId) {
        log.info("Marking notification {} as read for user: {}", id, userId);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        // Check ownership
        if (!notification.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Notification does not belong to user: " + userId);
        }

        // Check if already deleted
        if (notification.getDeletedAt() != null) {
            throw new IllegalArgumentException("Cannot mark deleted notification as read");
        }

        // Mark as read using helper method
        if (notification.isUnread()) {
            notification.markAsRead();
            notificationRepository.save(notification);
            log.info("Notification marked as read successfully");
        } else {
            log.debug("Notification {} is already read", id);
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        notificationRepository.markAllAsReadByUserId(userId, LocalDateTime.now());
        log.info("All notifications marked as read successfully");
    }

    @Override
    public void sendNotification(Long userId, NotificationType type, String title, String message,
            Map<String, Object> data) {
        log.info("Sending notification to user: {} with type: {}", userId, type);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Convert data map to JSON string
        String metadataJson = null;
        if (data != null && !data.isEmpty()) {
            try {
                metadataJson = objectMapper.writeValueAsString(data);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize data", e);
            }
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .priority(determinePriority(type))
                .title(title)
                .message(message)
                .metadata(metadataJson)
                .isRead(false)
                .isSent(true)
                .sentAt(LocalDateTime.now())
                .sendEmail(shouldSendEmail(type))
                .emailSent(false)
                .sendSms(shouldSendSms(type))
                .smsSent(false)
                .sendPush(shouldSendPush(type))
                .pushSent(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification sent successfully");
    }

    @Override
    public void sendBulkNotification(List<Long> userIds, NotificationType type, String title, String message) {
        log.info("Sending bulk notification to {} users with type: {}", userIds.size(), type);

        List<User> users = userRepository.findAllById(userIds);

        if (users.size() != userIds.size()) {
            log.warn("Some users not found. Expected: {}, Found: {}", userIds.size(), users.size());
        }

        List<Notification> notifications = users.stream()
                .map(user -> Notification.builder()
                        .user(user)
                        .type(type)
                        .priority(determinePriority(type))
                        .title(title)
                        .message(message)
                        .isRead(false)
                        .isSent(true)
                        .sentAt(LocalDateTime.now())
                        .sendEmail(shouldSendEmail(type))
                        .emailSent(false)
                        .sendSms(shouldSendSms(type))
                        .smsSent(false)
                        .sendPush(shouldSendPush(type))
                        .pushSent(false)
                        .build())
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
        log.info("Bulk notifications sent successfully to {} users", notifications.size());
    }

    @Override
    public void processPendingEmailNotifications() {
        log.info("Processing pending email notifications");

        List<Notification> notifications = notificationRepository
                .findPendingEmailNotifications(PageRequest.of(0, 100));

        int successCount = 0;
        int failureCount = 0;

        for (Notification notification : notifications) {
            try {
                // TODO: Implement actual email sending logic
                // Example: emailService.sendEmail(notification.getUser().getEmail(),
                // notification.getTitle(), notification.getMessage());

                notification.markEmailSent();
                notificationRepository.save(notification);
                successCount++;

                log.debug("Email sent successfully for notification: {}", notification.getId());
            } catch (Exception e) {
                failureCount++;
                log.error("Failed to send email for notification: {}", notification.getId(), e);
            }
        }

        log.info("Processed {} email notifications (Success: {}, Failed: {})",
                notifications.size(), successCount, failureCount);
    }

    @Override
    public void processPendingSmsNotifications() {
        log.info("Processing pending SMS notifications");

        List<Notification> notifications = notificationRepository
                .findPendingSmsNotifications(PageRequest.of(0, 50));

        int successCount = 0;
        int failureCount = 0;

        for (Notification notification : notifications) {
            try {
                // TODO: Implement actual SMS sending logic
                // Example: smsService.sendSms(notification.getUser().getPhone(),
                // notification.getMessage());

                notification.markSmsSent();
                notificationRepository.save(notification);
                successCount++;

                log.debug("SMS sent successfully for notification: {}", notification.getId());
            } catch (Exception e) {
                failureCount++;
                log.error("Failed to send SMS for notification: {}", notification.getId(), e);
            }
        }

        log.info("Processed {} SMS notifications (Success: {}, Failed: {})",
                notifications.size(), successCount, failureCount);
    }

    @Override
    public void processPendingPushNotifications() {
        log.info("Processing pending push notifications");

        List<Notification> notifications = notificationRepository
                .findPendingPushNotifications(PageRequest.of(0, 200));

        int successCount = 0;
        int failureCount = 0;

        for (Notification notification : notifications) {
            try {
                // TODO: Implement actual push notification sending logic
                // Example: pushService.sendPush(notification.getUser().getDeviceToken(),
                // notification.getTitle(), notification.getMessage());

                notification.markPushSent();
                notification.markAsSent(); // Mark overall as sent when push is sent
                notificationRepository.save(notification);
                successCount++;

                log.debug("Push notification sent successfully for notification: {}", notification.getId());
            } catch (Exception e) {
                failureCount++;
                log.error("Failed to send push notification for notification: {}", notification.getId(), e);
            }
        }

        log.info("Processed {} push notifications (Success: {}, Failed: {})",
                notifications.size(), successCount, failureCount);
    }

    @Override
    public void deleteOldReadNotifications(Long userId, Integer daysOld) {
        log.info("Deleting old read notifications for user: {} older than {} days", userId, daysOld);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        if (daysOld == null || daysOld < 1) {
            throw new IllegalArgumentException("Days old must be at least 1");
        }

        LocalDateTime before = LocalDateTime.now().minusDays(daysOld);
        notificationRepository.deleteOldReadNotifications(userId, before, LocalDateTime.now());

        log.info("Old read notifications deleted successfully");
    }

    @Override
    public void deleteExpiredNotifications() {
        log.info("Deleting expired notifications");

        notificationRepository.deleteExpiredNotifications(LocalDateTime.now());

        log.info("Expired notifications deleted successfully");
    }

    // ==================== HELPER METHODS ====================

    /**
     * Determine notification priority based on type
     */
    private NotificationPriority determinePriority(NotificationType type) {
        return switch (type) {
            // HIGH priority - urgent actions required
            case PAYMENT_FAILED, BOOKING_CANCELLED, ORDER_CANCELLED,
                    REFUND_PROCESSED, MAINTENANCE, TOURNAMENT_MATCH ->
                NotificationPriority.HIGH;

            // NORMAL priority - important but not urgent
            case BOOKING_CONFIRMED, BOOKING_REMINDER, BOOKING_COMPLETED,
                    ORDER_PLACED, ORDER_CONFIRMED, ORDER_SHIPPED, ORDER_DELIVERED,
                    PAYMENT_SUCCESS,
                    TOURNAMENT_REGISTRATION, TOURNAMENT_STARTED, TOURNAMENT_RESULT,
                    POINTS_EARNED, POINTS_REDEEMED, TIER_UPGRADED,
                    SYSTEM_ANNOUNCEMENT,
                    NEW_MESSAGE, REVIEW_REPLY, REVIEW_RESPONDED ->
                NotificationPriority.NORMAL;

            // LOW priority - informational only
            case PROMOTION_AVAILABLE, PROMOTION_EXPIRING,
                    POINTS_EXPIRING, POINTS_EXPIRED,
                    NEW_FOLLOWER, MENTION,
                    REVIEW_APPROVED, REVIEW_REJECTED, REVIEW_HELPFUL ->
                NotificationPriority.LOW;

            default -> NotificationPriority.NORMAL;
        };
    }

    /**
     * Determine if email should be sent for this notification type
     */
    private boolean shouldSendEmail(NotificationType type) {
        return switch (type) {
            // Send email for important transactional events
            case BOOKING_CONFIRMED, BOOKING_CANCELLED, BOOKING_REMINDER,
                    ORDER_PLACED, ORDER_CONFIRMED, ORDER_SHIPPED, ORDER_DELIVERED, ORDER_CANCELLED,
                    PAYMENT_SUCCESS, PAYMENT_FAILED, REFUND_PROCESSED,
                    TOURNAMENT_REGISTRATION, TOURNAMENT_STARTED,
                    TIER_UPGRADED,
                    MAINTENANCE ->
                true;

            default -> false;
        };
    }

    /**
     * Determine if SMS should be sent for this notification type
     */
    private boolean shouldSendSms(NotificationType type) {
        return switch (type) {
            // Send SMS only for critical and time-sensitive events
            case BOOKING_CONFIRMED, BOOKING_CANCELLED, BOOKING_REMINDER,
                    PAYMENT_FAILED, REFUND_PROCESSED,
                    TOURNAMENT_MATCH, // Match starting soon
                    MAINTENANCE ->
                true;

            default -> false;
        };
    }

    /**
     * Determine if push notification should be sent for this notification type
     */
    private boolean shouldSendPush(NotificationType type) {
        // Send push for all notification types by default
        return true;
    }
}