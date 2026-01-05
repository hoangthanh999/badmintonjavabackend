package com.hoangthanhhong.badminton.dto.maintenance;

import com.hoangthanhhong.badminton.enums.MaintenanceStatus;
import com.hoangthanhhong.badminton.enums.MaintenanceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceScheduleDTO {

    private Long maintenanceId;
    private Long courtId;
    private String courtName;
    private MaintenanceType maintenanceType;
    private LocalDateTime scheduledStartDate;
    private LocalDateTime scheduledEndDate;
    private MaintenanceStatus status;
    private String priority;
    private String assignedTo;
}
