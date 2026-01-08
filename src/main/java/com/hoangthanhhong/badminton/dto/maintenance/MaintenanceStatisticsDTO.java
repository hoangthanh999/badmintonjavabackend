// File: MaintenanceStatisticsDTO.java (CẬP NHẬT)
package com.hoangthanhhong.badminton.dto.maintenance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceStatisticsDTO {
    private Long courtId;
    private String courtName;
    private LocalDate startDate;
    private LocalDate endDate;

    // Counts - ✅ ĐỔI TẤT CẢ từ Integer sang Long
    private Long totalMaintenance;
    private Long completedMaintenance;
    private Long inProgressMaintenance;
    private Long scheduledMaintenance;
    private Long cancelledMaintenance;
    private Long overdueMaintenance;

    // Costs
    private Double totalCost;
    private Double averageCost;
    private Double minCost;
    private Double maxCost;

    // Duration
    private Double averageDuration;
    private Integer totalDuration;

    // By type
    private List<Object[]> typeStatistics;

    // By status
    private List<Object[]> statusCounts;
}
