package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_entity_type", columnList = "entity_type"),
        @Index(name = "idx_entity_id", columnList = "entity_id"),
        @Index(name = "idx_action", columnList = "action"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", length = 100)
    private String username;

    @Column(name = "user_email", length = 100)
    private String userEmail;

    @Column(name = "user_role", length = 50)
    private String userRole;

    // === ACTION ===

    @Column(nullable = false, length = 50)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.

    @Column(name = "action_category", length = 50)
    private String actionCategory; // AUTHENTICATION, BOOKING, ORDER, USER_MANAGEMENT, etc.

    @Column(columnDefinition = "TEXT")
    private String description;

    // === ENTITY ===

    @Column(name = "entity_type", length = 100)
    private String entityType; // User, Booking, Order, etc.

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_name", length = 200)
    private String entityName;

    // === CHANGES ===

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue; // JSON

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue; // JSON

    @Column(name = "changes", columnDefinition = "TEXT")
    private String changes; // JSON array of changes

    // === REQUEST INFO ===

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "request_method", length = 10)
    private String requestMethod; // GET, POST, PUT, DELETE

    @Column(name = "request_url", length = 500)
    private String requestUrl;

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "response_time")
    private Long responseTime; // in milliseconds

    // === LOCATION ===

    @Column(length = 100)
    private String country;

    @Column(length = 100)
    private String city;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    // === DEVICE INFO ===

    @Column(name = "device_type", length = 50)
    private String deviceType; // DESKTOP, MOBILE, TABLET

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "browser", length = 100)
    private String browser;

    // === STATUS ===

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "SUCCESS"; // SUCCESS, FAILURE, ERROR

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    // === TIMESTAMP ===

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    // === METADATA ===

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON for additional data

    @Column(name = "is_sensitive")
    @Builder.Default
    private Boolean isSensitive = false;

    @Column(name = "is_flagged")
    @Builder.Default
    private Boolean isFlagged = false;

    @Column(name = "flag_reason", columnDefinition = "TEXT")
    private String flagReason;

    // === HELPER METHODS ===

    @PrePersist
    public void setTimestamp() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public boolean isSuccess() {
        return "SUCCESS".equals(status);
    }

    public boolean isFailure() {
        return "FAILURE".equals(status) || "ERROR".equals(status);
    }

    public void flag(String reason) {
        this.isFlagged = true;
        this.flagReason = reason;
    }
}
