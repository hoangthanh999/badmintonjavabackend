package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.MaintenanceStatus;
import com.hoangthanhhong.badminton.enums.MaintenanceType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "maintenances", indexes = {
        @Index(name = "idx_court_id", columnList = "court_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_scheduled_date", columnList = "scheduled_date"),
        @Index(name = "idx_type", columnList = "type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private MaintenanceType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private MaintenanceStatus status = MaintenanceStatus.SCHEDULED;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDate scheduledDate;

    @Column(name = "scheduled_start_time")
    private java.time.LocalTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private java.time.LocalTime scheduledEndTime;

    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Column(name = "estimated_duration")
    private Integer estimatedDuration; // in minutes

    @Column(name = "actual_duration")
    private Integer actualDuration; // in minutes

    // === COST ===

    @Column(name = "estimated_cost")
    private Double estimatedCost;

    @Column(name = "actual_cost")
    private Double actualCost;

    @Column(length = 20)
    private String currency;

    // === PERSONNEL ===

    @Column(name = "assigned_to_id")
    private Long assignedToId;

    @Column(name = "assigned_to_name", length = 100)
    private String assignedToName;

    @Column(name = "technician_name", length = 100)
    private String technicianName;

    @Column(name = "technician_phone", length = 20)
    private String technicianPhone;

    @Column(name = "vendor_name", length = 200)
    private String vendorName;

    @Column(name = "vendor_contact", length = 100)
    private String vendorContact;

    // === PRIORITY & SEVERITY ===

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String priority = "NORMAL"; // LOW, NORMAL, HIGH, URGENT

    @Column(length = 20)
    private String severity; // MINOR, MODERATE, MAJOR, CRITICAL

    @Column(name = "is_emergency")
    @Builder.Default
    private Boolean isEmergency = false;

    @Column(name = "is_recurring")
    @Builder.Default
    private Boolean isRecurring = false;

    @Column(name = "recurrence_pattern", length = 100)
    private String recurrencePattern; // DAILY, WEEKLY, MONTHLY, QUARTERLY, YEARLY

    @Column(name = "next_maintenance_date")
    private LocalDate nextMaintenanceDate;

    // === DETAILS ===

    @Column(name = "work_performed", columnDefinition = "TEXT")
    private String workPerformed;

    @Column(name = "parts_replaced", columnDefinition = "TEXT")
    private String partsReplaced;

    @Column(name = "materials_used", columnDefinition = "TEXT")
    private String materialsUsed;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completion_notes", columnDefinition = "TEXT")
    private String completionNotes;

    // === IMAGES ===

    @ElementCollection
    @CollectionTable(name = "maintenance_images", joinColumns = @JoinColumn(name = "maintenance_id"))
    @Column(name = "image_url", length = 500)
    @Builder.Default
    private List<String> beforeImages = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "maintenance_after_images", joinColumns = @JoinColumn(name = "maintenance_id"))
    @Column(name = "image_url", length = 500)
    @Builder.Default
    private List<String> afterImages = new ArrayList<>();

    // === APPROVAL ===

    @Column(name = "requires_approval")
    @Builder.Default
    private Boolean requiresApproval = false;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approval_notes", columnDefinition = "TEXT")
    private String approvalNotes;

    // === CANCELLATION ===

    @Column(name = "cancelled_by")
    private Long cancelledBy;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "maintenance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaintenanceChecklistItem> checklistItems = new ArrayList<>();

    @OneToMany(mappedBy = "maintenance", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MaintenanceLog> logs = new ArrayList<>();

    // === HELPER METHODS ===

    public void start() {
        this.status = MaintenanceStatus.IN_PROGRESS;
        this.actualStartTime = LocalDateTime.now();
    }

    public void complete(String completionNotes) {
        this.status = MaintenanceStatus.COMPLETED;
        this.actualEndTime = LocalDateTime.now();
        this.completionNotes = completionNotes;

        if (actualStartTime != null) {
            this.actualDuration = (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }

        // Schedule next maintenance if recurring
        if (isRecurring && nextMaintenanceDate != null) {
            scheduleNextMaintenance();
        }
    }

    public void cancel(Long cancelledBy, String reason) {
        this.status = MaintenanceStatus.CANCELLED;
        this.cancelledBy = cancelledBy;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
    }

    public void postpone(LocalDate newDate) {
        this.status = MaintenanceStatus.POSTPONED;
        this.scheduledDate = newDate;
    }

    public void approve(Long approvedBy, String notes) {
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
        this.approvalNotes = notes;
    }

    public void markAsEmergency() {
        this.isEmergency = true;
        this.priority = "URGENT";
    }

    private void scheduleNextMaintenance() {
        if (recurrencePattern == null)
            return;

        switch (recurrencePattern) {
            case "DAILY":
                this.nextMaintenanceDate = scheduledDate.plusDays(1);
                break;
            case "WEEKLY":
                this.nextMaintenanceDate = scheduledDate.plusWeeks(1);
                break;
            case "MONTHLY":
                this.nextMaintenanceDate = scheduledDate.plusMonths(1);
                break;
            case "QUARTERLY":
                this.nextMaintenanceDate = scheduledDate.plusMonths(3);
                break;
            case "YEARLY":
                this.nextMaintenanceDate = scheduledDate.plusYears(1);
                break;
        }
    }

    public Integer getDurationInMinutes() {
        if (actualDuration != null)
            return actualDuration;
        if (actualStartTime != null && actualEndTime != null) {
            return (int) java.time.Duration.between(actualStartTime, actualEndTime).toMinutes();
        }
        return estimatedDuration;
    }

    public Double getCostVariance() {
        if (actualCost != null && estimatedCost != null) {
            return actualCost - estimatedCost;
        }
        return null;
    }

    public boolean isOverBudget() {
        if (actualCost != null && estimatedCost != null) {
            return actualCost > estimatedCost;
        }
        return false;
    }

    public boolean isOverdue() {
        if (status == MaintenanceStatus.SCHEDULED || status == MaintenanceStatus.POSTPONED) {
            return LocalDate.now().isAfter(scheduledDate);
        }
        return false;
    }

    public Integer getCompletionPercentage() {
        if (checklistItems.isEmpty())
            return 0;

        long completedItems = checklistItems.stream()
                .filter(MaintenanceChecklistItem::getIsCompleted)
                .count();

        return (int) ((completedItems * 100) / checklistItems.size());
    }
}
