package com.hoangthanhhong.badminton.dto;

import com.hoangthanhhong.badminton.enums.MaintenanceStatus;
import com.hoangthanhhong.badminton.enums.MaintenanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {

    private Long id;
    private Long courtId;
    private String courtName;
    private MaintenanceType type;
    private String title;
    private String description;
    private MaintenanceStatus status;

    private LocalDate scheduledDate;
    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;

    private Integer estimatedDuration;
    private Integer actualDuration;
    private Double estimatedCost;
    private Double actualCost;
    private String currency;

    private Long assignedToId;
    private String assignedToName;
    private String technicianName;
    private String technicianPhone;
    private String vendorName;
    private String vendorContact;

    private String priority;
    private String severity;
    private Boolean isEmergency;
    private Boolean isRecurring;
    private String recurrencePattern;
    private LocalDate nextMaintenanceDate;

    private String workPerformed;
    private String partsReplaced;
    private String materialsUsed;
    private String notes;
    private String completionNotes;

    private List<String> beforeImages;
    private List<String> afterImages;

    private Boolean requiresApproval;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String approvalNotes;

    private Long cancelledBy;
    private LocalDateTime cancelledAt;
    private String cancellationReason;

    private Integer completionPercentage;
    private List<MaintenanceChecklistItemDTO> checklistItems;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
