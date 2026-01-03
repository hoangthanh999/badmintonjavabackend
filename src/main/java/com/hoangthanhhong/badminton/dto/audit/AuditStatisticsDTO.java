package com.hoangthanhhong.badminton.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditStatisticsDTO {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Long totalActions;
    private Long successfulActions;
    private Long failedActions;

    private List<Object[]> actionCounts;
    private List<Object[]> userActivity;

    private Double averageResponseTime;
    private Long totalUsers;

    private Map<String, Long> actionsByCategory;
    private Map<String, Long> actionsByHour;
    private Map<String, Long> topUsers;
}
