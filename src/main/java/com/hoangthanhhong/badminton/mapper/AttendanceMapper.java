package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.attendance.AttendanceDTO;
import com.hoangthanhhong.badminton.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceDTO toDTO(Attendance attendance) {
        if (attendance == null)
            return null;

        return AttendanceDTO.builder()
                .id(attendance.getId())
                .userId(attendance.getUser() != null ? attendance.getUser().getId() : null)
                .userName(attendance.getUser() != null ? attendance.getUser().getName() : null)
                .date(attendance.getDate())
                .status(attendance.getStatus())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .checkInLocation(attendance.getCheckInLocation())
                .checkOutLocation(attendance.getCheckOutLocation())
                .scheduledStartTime(attendance.getScheduledStartTime())
                .scheduledEndTime(attendance.getScheduledEndTime())
                .workHours(attendance.getWorkHours())
                .overtimeHours(attendance.getOvertimeHours())
                .breakHours(attendance.getBreakHours())
                .isLate(attendance.getIsLate())
                .lateMinutes(attendance.getLateMinutes())
                .isEarlyDeparture(attendance.getIsEarlyDeparture())
                .earlyDepartureMinutes(attendance.getEarlyDepartureMinutes())
                .leaveType(attendance.getLeaveType())
                .leaveReason(attendance.getLeaveReason())
                .isApproved(attendance.getIsApproved())
                .approvedBy(attendance.getApprovedBy())
                .approvedAt(attendance.getApprovedAt())
                .notes(attendance.getNotes())
                .adminNotes(attendance.getAdminNotes())
                .isVerified(attendance.getIsVerified())
                .verifiedBy(attendance.getVerifiedBy())
                .verifiedAt(attendance.getVerifiedAt())
                .shiftId(attendance.getShift() != null ? attendance.getShift().getId() : null)
                .shiftName(attendance.getShift() != null ? attendance.getShift().getName() : null)
                .createdAt(attendance.getCreatedAt())
                .updatedAt(attendance.getUpdatedAt())
                .build();
    }
}
