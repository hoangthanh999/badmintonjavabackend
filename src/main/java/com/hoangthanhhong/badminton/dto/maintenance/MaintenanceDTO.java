// File: MaintenanceDTO.java (CẬP NHẬT - Thêm cuối file)
package com.hoangthanhhong.badminton.dto.maintenance;

import com.hoangthanhhong.badminton.enums.MaintenanceStatus;
import com.hoangthanhhong.badminton.enums.MaintenanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List; // ✅ THÊM IMPORT

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {
    private Long id;

    // Court
    private Long courtId;
    private String courtName;

    // Type & Status
    private MaintenanceType type;
    private MaintenanceStatus status;

    // Basic info
    private String title;
    private String description;

    // Schedule
    private LocalDate scheduledDate;
    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;

    // Duration
    private Integer estimatedDuration;
    private Integer actualDuration;

    // Cost
    private Double estimatedCost;
    private Double actualCost;
    private String currency;

    // Assignment
    private Long assignedToId;
    private String assignedToName;
    private String technicianName;
    private String technicianPhone;
    private String vendorName;
    private String vendorContact;

    // Priority
    private String priority;
    private String severity;
    private Boolean isEmergency;

    // Recurrence
    private Boolean isRecurring;
    private String recurrencePattern;
    private LocalDate nextMaintenanceDate;

    // Work details
    private String workPerformed;
    private String partsReplaced;
    private String materialsUsed;
    private String notes;
    private String completionNotes;

    // Images
    private List<String> beforeImages;
    private List<String> afterImages;

    // Approval
    private Boolean requiresApproval;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String approvalNotes;

    // Cancellation
    private Long cancelledBy;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    // Progress
    private Integer completionPercentage;

    // Checklist - ✅ THÊM FIELD NÀY
    private List<MaintenanceChecklistItemDTO> checklistItems;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
