package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.NotificationPriority;
import com.hoangthanhhong.badminton.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_is_read", columnList = "is_read"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private NotificationPriority priority = NotificationPriority.NORMAL;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "is_sent")
    @Builder.Default
    private Boolean isSent = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // === NOTIFICATION CHANNELS ===

    @Column(name = "send_email")
    @Builder.Default
    private Boolean sendEmail = false;

    @Column(name = "email_sent")
    @Builder.Default
    private Boolean emailSent = false;

    @Column(name = "send_sms")
    @Builder.Default
    private Boolean sendSms = false;

    @Column(name = "sms_sent")
    @Builder.Default
    private Boolean smsSent = false;

    @Column(name = "send_push")
    @Builder.Default
    private Boolean sendPush = true;

    @Column(name = "push_sent")
    @Builder.Default
    private Boolean pushSent = false;

    // === ADDITIONAL DATA ===

    @Column(name = "action_url", length = 500)
    private String actionUrl;

    @Column(name = "action_text", length = 100)
    private String actionText;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "icon", length = 50)
    private String icon;

    // === RELATED ENTITIES ===

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType; // BOOKING, ORDER, TOURNAMENT, etc.

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    // === METADATA (JSON) ===

    @Column(columnDefinition = "TEXT")
    private String metadata;

    // === EXPIRATION ===

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    // === HELPER METHODS ===

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markAsSent() {
        this.isSent = true;
        this.sentAt = LocalDateTime.now();
    }

    public void markEmailSent() {
        this.emailSent = true;
    }

    public void markSmsSent() {
        this.smsSent = true;
    }

    public void markPushSent() {
        this.pushSent = true;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public boolean isUnread() {
        return !isRead;
    }

    public boolean shouldSendEmail() {
        return sendEmail && !emailSent;
    }

    public boolean shouldSendSms() {
        return sendSms && !smsSent;
    }

    public boolean shouldSendPush() {
        return sendPush && !pushSent;
    }
}
