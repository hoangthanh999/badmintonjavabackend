package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceChecklistItemDTO;
import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceDTO;
import com.hoangthanhhong.badminton.entity.Maintenance;
import com.hoangthanhhong.badminton.entity.MaintenanceChecklistItem;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MaintenanceMapper {

    public MaintenanceDTO toDTO(Maintenance maintenance) {
        if (maintenance == null)
            return null;

        return MaintenanceDTO.builder()
                .id(maintenance.getId())
                .courtId(maintenance.getCourt() != null ? maintenance.getCourt().getId() : null)
                .courtName(maintenance.getCourt() != null ? maintenance.getCourt().getName() : null)
                .type(maintenance.getType())
                .title(maintenance.getTitle())
                .description(maintenance.getDescription())
                .status(maintenance.getStatus())
                .scheduledDate(maintenance.getScheduledDate())
                .scheduledStartTime(maintenance.getScheduledStartTime())
                .scheduledEndTime(maintenance.getScheduledEndTime())
                .actualStartTime(maintenance.getActualStartTime())
                .actualEndTime(maintenance.getActualEndTime())
                .estimatedDuration(maintenance.getEstimatedDuration())
                .actualDuration(maintenance.getActualDuration())
                .estimatedCost(maintenance.getEstimatedCost())
                .actualCost(maintenance.getActualCost())
                .currency(maintenance.getCurrency())
                .assignedToId(maintenance.getAssignedToId())
                .assignedToName(maintenance.getAssignedToName())
                .technicianName(maintenance.getTechnicianName())
                .technicianPhone(maintenance.getTechnicianPhone())
                .vendorName(maintenance.getVendorName())
                .vendorContact(maintenance.getVendorContact())
                .priority(maintenance.getPriority())
                .severity(maintenance.getSeverity())
                .isEmergency(maintenance.getIsEmergency())
                .isRecurring(maintenance.getIsRecurring())
                .recurrencePattern(maintenance.getRecurrencePattern())
                .nextMaintenanceDate(maintenance.getNextMaintenanceDate())
                .workPerformed(maintenance.getWorkPerformed())
                .partsReplaced(maintenance.getPartsReplaced())
                .materialsUsed(maintenance.getMaterialsUsed())
                .notes(maintenance.getNotes())
                .completionNotes(maintenance.getCompletionNotes())
                .beforeImages(maintenance.getBeforeImages())
                .afterImages(maintenance.getAfterImages())
                .requiresApproval(maintenance.getRequiresApproval())
                .approvedBy(maintenance.getApprovedBy())
                .approvedAt(maintenance.getApprovedAt())
                .approvalNotes(maintenance.getApprovalNotes())
                .cancelledBy(maintenance.getCancelledBy())
                .cancelledAt(maintenance.getCancelledAt())
                .cancellationReason(maintenance.getCancellationReason())
                .completionPercentage(maintenance.getCompletionPercentage())
                .createdAt(maintenance.getCreatedAt())
                .updatedAt(maintenance.getUpdatedAt())
                .build();
    }

    public MaintenanceDTO toDTOWithDetails(Maintenance maintenance) {
        MaintenanceDTO dto = toDTO(maintenance);

        if (maintenance.getChecklistItems() != null && !maintenance.getChecklistItems().isEmpty()) {
            dto.setChecklistItems(
                    maintenance.getChecklistItems().stream()
                            .map(this::toChecklistItemDTO)
                            .collect(Collectors.toList()));
        }

        return dto;
    }

    public MaintenanceChecklistItemDTO toChecklistItemDTO(MaintenanceChecklistItem item) {
        if (item == null)
            return null;

        return MaintenanceChecklistItemDTO.builder()
                .id(item.getId())
                .task(item.getTask())
                .description(item.getDescription())
                .isCompleted(item.getIsCompleted())
                .completedAt(item.getCompletedAt())
                .completedBy(item.getCompletedBy())
                .completedByName(item.getCompletedByName())
                .notes(item.getNotes())
                .sortOrder(item.getSortOrder())
                .isMandatory(item.getIsMandatory())
                .build();
    }
}
