package com.hoangthanhhong.badminton.dto.attendance;

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
public class AttendanceStatisticsDTO {

    private Long userId;
    private String userName;
    private LocalDate startDate;
    private LocalDate endDate;

    private Integer totalDays;
    private Integer presentDays;
    private Integer absentDays;
    private Integer leaveDays;
    private Integer lateDays;
    private Integer halfDays;

    private Double totalWorkHours;
    private Double totalOvertimeHours;
    private Double averageWorkHours;

    private Double attendanceRate;
    private Double punctualityRate;

    private List<Object[]> statusCounts;
}
