package com.hoangthanhhong.badminton.dto.maintenance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceChecklistItemDTO {
    private Long id;
    private String task;
    private String description;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private Long completedBy;
    private String completedByName;
    private String notes;
    private Integer sortOrder;
    private Boolean isMandatory;
}
