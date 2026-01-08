// File: NotificationDTO.java (CẬP NHẬT)
package com.hoangthanhhong.badminton.dto.notification;

import com.hoangthanhhong.badminton.enums.NotificationPriority;
import com.hoangthanhhong.badminton.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;

    // User - ✅ THÊM FIELD NÀY
    private Long userId;
    private String userName;

    // Type & Priority
    private NotificationType type;
    private NotificationPriority priority;

    // Content
    private String title;
    private String message;

    // Status
    private Boolean isRead;
    private LocalDateTime readAt;

    private Boolean isSent;
    private LocalDateTime sentAt;

    // Channels
    private Boolean sendEmail;
    private Boolean emailSent;

    private Boolean sendSms;
    private Boolean smsSent;

    private Boolean sendPush;
    private Boolean pushSent;

    // Action
    private String actionUrl;
    private String actionText;

    // Media
    private String imageUrl;
    private String icon;

    // Related entity
    private String relatedEntityType;
    private Long relatedEntityId;

    // Metadata
    private String metadata;

    // Expiration
    private LocalDateTime expiresAt;
    private Boolean isExpired;

    // Timestamps
    private LocalDateTime createdAt;
}
