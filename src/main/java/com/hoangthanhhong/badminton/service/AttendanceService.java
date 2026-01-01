package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.attendance.AttendanceDTO;
import com.hoangthanhhong.badminton.dto.attendance.AttendanceStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.attendance.CheckInRequest;
import com.hoangthanhhong.badminton.dto.request.attendance.CheckOutRequest;
import com.hoangthanhhong.badminton.dto.request.attendance.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    // Check in/out
    AttendanceDTO checkIn(CheckInRequest request, Long userId);

    AttendanceDTO checkOut(CheckOutRequest request, Long userId);

    // Leave management
    AttendanceDTO requestLeave(LeaveRequest request, Long userId);

    void approveLeave(Long attendanceId, Long approvedBy);

    void rejectLeave(Long attendanceId, Long rejectedBy, String reason);

    // Get attendance
    AttendanceDTO getAttendanceById(Long id);

    AttendanceDTO getUserAttendanceForDate(Long userId, LocalDate date);

    List<AttendanceDTO> getUserAttendance(Long userId, LocalDate startDate, LocalDate endDate);

    Page<AttendanceDTO> getUserAttendance(Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Statistics
    AttendanceStatisticsDTO getUserAttendanceStatistics(Long userId, LocalDate startDate, LocalDate endDate);

    List<AttendanceStatisticsDTO> getAllUsersAttendanceStatistics(LocalDate startDate, LocalDate endDate);

    // Verification
    void verifyAttendance(Long attendanceId, Long verifiedBy);

    // Admin actions
    void markAsAbsent(Long userId, LocalDate date, String reason);

    void markAsHoliday(LocalDate date);
}
