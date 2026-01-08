// File: NotificationMapper.java
package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.notification.NotificationDTO;
import com.hoangthanhhong.badminton.entity.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification notification) {
        if (notification == null)
            return null;

        Boolean isExpired = notification.getExpiresAt() != null &&
                notification.getExpiresAt().isBefore(LocalDateTime.now());

        return NotificationDTO.builder()
                .id(notification.getId())
                .userId(notification.getUser().getId())
                .type(notification.getType())
                .priority(notification.getPriority())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .isRead(notification.getIsRead())
                .readAt(notification.getReadAt())
                .isSent(notification.getIsSent())
                .sentAt(notification.getSentAt())
                .sendEmail(notification.getSendEmail())
                .emailSent(notification.getEmailSent())
                .sendSms(notification.getSendSms())
                .smsSent(notification.getSmsSent())
                .sendPush(notification.getSendPush())
                .pushSent(notification.getPushSent())
                .actionUrl(notification.getActionUrl())
                .actionText(notification.getActionText())
                .imageUrl(notification.getImageUrl())
                .icon(notification.getIcon())
                .relatedEntityType(notification.getRelatedEntityType())
                .relatedEntityId(notification.getRelatedEntityId())
                .metadata(notification.getMetadata())
                .expiresAt(notification.getExpiresAt())
                .isExpired(isExpired)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
