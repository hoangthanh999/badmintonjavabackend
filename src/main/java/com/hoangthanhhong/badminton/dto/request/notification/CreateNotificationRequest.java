package com.hoangthanhhong.badminton.dto.request.notification;

import com.hoangthanhhong.badminton.enums.*;
import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationRequest {
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private NotificationPriority priority;
    private Boolean sendEmail;
    private Boolean sendSms;
    private Boolean sendPush;
    private String actionUrl;
    private Map<String, Object> metadata;
}
