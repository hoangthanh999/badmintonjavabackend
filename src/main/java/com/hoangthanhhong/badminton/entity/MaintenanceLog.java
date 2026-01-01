package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_logs", indexes = {
        @Index(name = "idx_maintenance_id", columnList = "maintenance_id"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_id", nullable = false)
    private Maintenance maintenance;

    @Column(nullable = false, length = 50)
    private String action; // CREATED, STARTED, UPDATED, COMPLETED, CANCELLED

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "performed_by")
    private Long performedBy;

    @Column(name = "performed_by_name", length = 100)
    private String performedByName;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @PrePersist
    public void setPerformedAt() {
        if (performedAt == null) {
            performedAt = LocalDateTime.now();
        }
    }
}
