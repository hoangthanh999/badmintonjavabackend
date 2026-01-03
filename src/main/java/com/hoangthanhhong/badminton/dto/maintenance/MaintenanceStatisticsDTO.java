package com.hoangthanhhong.badminton.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceStatisticsDTO {

    private Long courtId;
    private LocalDate startDate;
    private LocalDate endDate;

    private Integer totalMaintenance;
    private Integer completedMaintenance;
    private Integer inProgressMaintenance;
    private Integer scheduledMaintenance;
    private Integer overdueMaintenance;

    private Double totalCost;
    private Double averageCost;
    private Double totalEstimatedCost;
    private Double totalActualCost;
    private Double costVariance;

    private Integer totalDuration;
    private Integer averageDuration;

    private List<Object[]> typeStatistics;
    private List<Object[]> statusCounts;
    private Map<String, Integer> maintenanceByMonth;
}
