package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.notification.NotificationDTO;
import com.hoangthanhhong.badminton.dto.request.notification.CreateNotificationRequest;
import com.hoangthanhhong.badminton.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NotificationService {

    // CRUD
    NotificationDTO createNotification(CreateNotificationRequest request);

    void deleteNotification(Long id);

    NotificationDTO getNotificationById(Long id);

    // Get notifications
    Page<NotificationDTO> getUserNotifications(Long userId, Pageable pageable);

    List<NotificationDTO> getUnreadNotifications(Long userId);

    Long getUnreadCount(Long userId);

    // Mark as read
    void markAsRead(Long id, Long userId);

    void markAllAsRead(Long userId);

    // Send notifications
    void sendNotification(Long userId, NotificationType type, String title, String message, Map<String, Object> data);

    void sendBulkNotification(List<Long> userIds, NotificationType type, String title, String message);

    // Process pending notifications
    void processPendingEmailNotifications();

    void processPendingSmsNotifications();

    void processPendingPushNotifications();

    // Cleanup
    void deleteOldReadNotifications(Long userId, Integer daysOld);

    void deleteExpiredNotifications();
}
