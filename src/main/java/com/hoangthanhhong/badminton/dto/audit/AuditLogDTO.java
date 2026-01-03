package com.hoangthanhhong.badminton.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDTO {

    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private String userRole;

    private String action;
    private String actionCategory;
    private String description;

    private String entityType;
    private Long entityId;
    private String entityName;

    private String oldValue;
    private String newValue;
    private String changes;

    private String ipAddress;
    private String userAgent;
    private String requestMethod;
    private String requestUrl;

    private String country;
    private String city;

    private String deviceType;
    private String operatingSystem;
    private String browser;

    private String status;
    private String errorMessage;

    private LocalDateTime timestamp;
    private Long responseTime;

    private Boolean isSensitive;
    private Boolean isFlagged;
    private String flagReason;
}
