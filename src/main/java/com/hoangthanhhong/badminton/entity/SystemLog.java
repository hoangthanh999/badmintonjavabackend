package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "system_logs", indexes = {
        @Index(name = "idx_level", columnList = "level"),
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemLog extends BaseEntity {

    @Column(nullable = false, length = 20)
    private String level; // DEBUG, INFO, WARN, ERROR, FATAL

    @Column(nullable = false, length = 100)
    private String category; // DATABASE, API, PAYMENT, EMAIL, etc.

    @Column(nullable = false, length = 200)
    private String message;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "stack_trace", columnDefinition = "TEXT")
    private String stackTrace;

    @Column(name = "source_class", length = 200)
    private String sourceClass;

    @Column(name = "source_method", length = 100)
    private String sourceMethod;

    @Column(name = "thread_name", length = 100)
    private String threadName;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "server_name", length = 100)
    private String serverName;

    @Column(name = "environment", length = 50)
    private String environment; // DEV, TEST, STAGING, PRODUCTION

    @Column(columnDefinition = "TEXT")
    private String metadata; // JSON

    @Column(name = "is_resolved")
    @Builder.Default
    private Boolean isResolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by")
    private Long resolvedBy;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @PrePersist
    public void setTimestamp() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public void resolve(Long resolvedBy, String notes) {
        this.isResolved = true;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolvedBy;
        this.resolutionNotes = notes;
    }

    public boolean isError() {
        return "ERROR".equals(level) || "FATAL".equals(level);
    }
}
