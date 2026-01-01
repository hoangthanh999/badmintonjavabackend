package com.hoangthanhhong.badminton.controller;

import com.hoangthanhhong.badminton.dto.attendance.AttendanceDTO;
import com.hoangthanhhong.badminton.dto.attendance.AttendanceStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.attendance.CheckInRequest;
import com.hoangthanhhong.badminton.dto.request.attendance.CheckOutRequest;
import com.hoangthanhhong.badminton.dto.request.attendance.LeaveRequest;
import com.hoangthanhhong.badminton.dto.response.ApiResponse;
import com.hoangthanhhong.badminton.security.UserPrincipal;
import com.hoangthanhhong.badminton.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance Management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Check in", description = "Check in for attendance")
    public ResponseEntity<ApiResponse<AttendanceDTO>> checkIn(
            @Valid @RequestBody CheckInRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AttendanceDTO attendance = attendanceService.checkIn(request, userPrincipal.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(attendance, "Checked in successfully"));
    }

    @PostMapping("/check-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Check out", description = "Check out from attendance")
    public ResponseEntity<ApiResponse<AttendanceDTO>> checkOut(
            @Valid @RequestBody CheckOutRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AttendanceDTO attendance = attendanceService.checkOut(request, userPrincipal.getId());

        return ResponseEntity.ok(
                ApiResponse.success(attendance, "Checked out successfully"));
    }

    @PostMapping("/leave")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Request leave", description = "Request leave for a specific date")
    public ResponseEntity<ApiResponse<AttendanceDTO>> requestLeave(
            @Valid @RequestBody LeaveRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AttendanceDTO attendance = attendanceService.requestLeave(request, userPrincipal.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(attendance, "Leave request submitted successfully"));
    }

    @PostMapping("/{id}/approve-leave")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Approve leave", description = "Approve a leave request")
    public ResponseEntity<ApiResponse<Void>> approveLeave(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        attendanceService.approveLeave(id, userPrincipal.getId());

        return ResponseEntity.ok(
                ApiResponse.success(null, "Leave approved successfully"));
    }

    @PostMapping("/{id}/reject-leave")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Reject leave", description = "Reject a leave request")
    public ResponseEntity<ApiResponse<Void>> rejectLeave(
            @PathVariable Long id,
            @RequestParam String reason,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        attendanceService.rejectLeave(id, userPrincipal.getId(), reason);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Leave rejected successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get attendance by ID", description = "Get attendance details by ID")
    public ResponseEntity<ApiResponse<AttendanceDTO>> getAttendanceById(@PathVariable Long id) {
        AttendanceDTO attendance = attendanceService.getAttendanceById(id);

        return ResponseEntity.ok(ApiResponse.success(attendance));
    }

    @GetMapping("/me/today")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get my attendance today", description = "Get current user's attendance for today")
    public ResponseEntity<ApiResponse<AttendanceDTO>> getMyAttendanceToday(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AttendanceDTO attendance = attendanceService.getUserAttendanceForDate(
                userPrincipal.getId(), LocalDate.now());

        return ResponseEntity.ok(ApiResponse.success(attendance));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get my attendance", description = "Get current user's attendance records")
    public ResponseEntity<ApiResponse<Page<AttendanceDTO>>> getMyAttendance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {

        Page<AttendanceDTO> attendances = attendanceService.getUserAttendance(
                userPrincipal.getId(), startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.success(attendances));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get user attendance", description = "Get attendance records for a specific user")
    public ResponseEntity<ApiResponse<Page<AttendanceDTO>>> getUserAttendance(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {

        Page<AttendanceDTO> attendances = attendanceService.getUserAttendance(
                userId, startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.success(attendances));
    }

    @GetMapping("/me/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get my attendance statistics", description = "Get current user's attendance statistics")
    public ResponseEntity<ApiResponse<AttendanceStatisticsDTO>> getMyAttendanceStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AttendanceStatisticsDTO statistics = attendanceService.getUserAttendanceStatistics(
                userPrincipal.getId(), startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/user/{userId}/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get user attendance statistics", description = "Get attendance statistics for a specific user")
    public ResponseEntity<ApiResponse<AttendanceStatisticsDTO>> getUserAttendanceStatistics(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AttendanceStatisticsDTO statistics = attendanceService.getUserAttendanceStatistics(
                userId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/statistics/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all users attendance statistics", description = "Get attendance statistics for all users")
    public ResponseEntity<ApiResponse<List<AttendanceStatisticsDTO>>> getAllUsersAttendanceStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<AttendanceStatisticsDTO> statistics = attendanceService.getAllUsersAttendanceStatistics(
                startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @PostMapping("/{id}/verify")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Verify attendance", description = "Verify an attendance record")
    public ResponseEntity<ApiResponse<Void>> verifyAttendance(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        attendanceService.verifyAttendance(id, userPrincipal.getId());

        return ResponseEntity.ok(
                ApiResponse.success(null, "Attendance verified successfully"));
    }

    @PostMapping("/mark-absent")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Mark as absent", description = "Mark a user as absent for a specific date")
    public ResponseEntity<ApiResponse<Void>> markAsAbsent(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String reason) {

        attendanceService.markAsAbsent(userId, date, reason);

        return ResponseEntity.ok(
                ApiResponse.success(null, "User marked as absent successfully"));
    }

    @PostMapping("/mark-holiday")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mark as holiday", description = "Mark a date as holiday for all users")
    public ResponseEntity<ApiResponse<Void>> markAsHoliday(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        attendanceService.markAsHoliday(date);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Date marked as holiday successfully"));
    }
}
