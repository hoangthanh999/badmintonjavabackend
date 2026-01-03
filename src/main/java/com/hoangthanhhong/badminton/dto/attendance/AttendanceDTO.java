package com.hoangthanhhong.badminton.dto;

import com.hoangthanhhong.badminton.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceDTO {

    private Long id;
    private Long userId;
    private String userName;
    private LocalDate date;
    private AttendanceStatus status;

    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String checkInLocation;
    private String checkOutLocation;

    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;

    private Double workHours;
    private Double overtimeHours;
    private Double breakHours;

    private Boolean isLate;
    private Integer lateMinutes;
    private Boolean isEarlyDeparture;
    private Integer earlyDepartureMinutes;

    private String leaveType;
    private String leaveReason;
    private Boolean isApproved;
    private Long approvedBy;
    private LocalDateTime approvedAt;

    private String notes;
    private String adminNotes;

    private Boolean isVerified;
    private Long verifiedBy;
    private LocalDateTime verifiedAt;

    private Long shiftId;
    private String shiftName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
