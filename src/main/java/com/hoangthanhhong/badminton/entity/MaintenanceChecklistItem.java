package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "maintenance_checklist_items", indexes = {
        @Index(name = "idx_maintenance_id", columnList = "maintenance_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceChecklistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maintenance_id", nullable = false)
    private Maintenance maintenance;

    @Column(nullable = false, length = 200)
    private String task;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by")
    private Long completedBy;

    @Column(name = "completed_by_name", length = 100)
    private String completedByName;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_mandatory")
    @Builder.Default
    private Boolean isMandatory = true;

    // === HELPER METHODS ===

    public void complete(Long completedBy, String completedByName, String notes) {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
        this.completedBy = completedBy;
        this.completedByName = completedByName;
        this.notes = notes;
    }

    public void uncomplete() {
        this.isCompleted = false;
        this.completedAt = null;
        this.completedBy = null;
        this.completedByName = null;
    }
}
