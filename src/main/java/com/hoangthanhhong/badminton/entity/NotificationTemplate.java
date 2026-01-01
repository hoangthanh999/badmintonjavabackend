package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.NotificationType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "notification_templates", indexes = {
        @Index(name = "idx_type", columnList = "type"),
        @Index(name = "idx_code", columnList = "code")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplate extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    // === EMAIL TEMPLATE ===

    @Column(name = "email_subject", length = 200)
    private String emailSubject;

    @Column(name = "email_body", columnDefinition = "TEXT")
    private String emailBody;

    // === SMS TEMPLATE ===

    @Column(name = "sms_body", length = 500)
    private String smsBody;

    // === PUSH NOTIFICATION TEMPLATE ===

    @Column(name = "push_title", length = 200)
    private String pushTitle;

    @Column(name = "push_body", columnDefinition = "TEXT")
    private String pushBody;

    // === IN-APP TEMPLATE ===

    @Column(name = "app_title", length = 200)
    private String appTitle;

    @Column(name = "app_body", columnDefinition = "TEXT")
    private String appBody;

    // === SETTINGS ===

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "default_send_email")
    @Builder.Default
    private Boolean defaultSendEmail = false;

    @Column(name = "default_send_sms")
    @Builder.Default
    private Boolean defaultSendSms = false;

    @Column(name = "default_send_push")
    @Builder.Default
    private Boolean defaultSendPush = true;

    // === VARIABLES ===

    @Column(name = "available_variables", columnDefinition = "TEXT")
    private String availableVariables; // JSON array of variable names
}
