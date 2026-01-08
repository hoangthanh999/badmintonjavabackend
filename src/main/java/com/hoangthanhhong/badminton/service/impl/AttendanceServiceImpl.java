package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.attendance.AttendanceDTO;
import com.hoangthanhhong.badminton.dto.attendance.AttendanceStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.attendance.CheckInRequest;
import com.hoangthanhhong.badminton.dto.request.attendance.CheckOutRequest;
import com.hoangthanhhong.badminton.dto.request.attendance.LeaveRequest;
import com.hoangthanhhong.badminton.entity.Attendance;
import com.hoangthanhhong.badminton.entity.Shift;
import com.hoangthanhhong.badminton.entity.User;
import com.hoangthanhhong.badminton.enums.AttendanceStatus;
import com.hoangthanhhong.badminton.exception.BadRequestException;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.AttendanceMapper;
import com.hoangthanhhong.badminton.repository.AttendanceRepository;
import com.hoangthanhhong.badminton.repository.ShiftRepository;
import com.hoangthanhhong.badminton.repository.UserRepository;
import com.hoangthanhhong.badminton.service.AttendanceService;
import com.hoangthanhhong.badminton.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final AttendanceMapper attendanceMapper;
    private final NotificationService notificationService;

    @Override
    public AttendanceDTO checkIn(CheckInRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate today = LocalDate.now();

        // Check if already checked in today
        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndDate(userId, today);
        if (existingAttendance.isPresent() && existingAttendance.get().getCheckInTime() != null) {
            throw new BadRequestException("Already checked in today");
        }

        // Get shift
        Shift shift = null;
        if (request.getShiftId() != null) {
            shift = shiftRepository.findById(request.getShiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift not found"));
        } else {
            shift = shiftRepository.findDefaultShift().orElse(null);
        }

        Attendance attendance;
        if (existingAttendance.isPresent()) {
            attendance = existingAttendance.get();
        } else {
            attendance = Attendance.builder()
                    .user(user)
                    .date(today)
                    .shift(shift)
                    .build();

            if (shift != null) {
                attendance.setScheduledStartTime(shift.getStartTime());
                attendance.setScheduledEndTime(shift.getEndTime());
            }
        }

        // Check in
        attendance.checkIn(
                LocalTime.now(),
                request.getLocation(),
                request.getIpAddress(),
                request.getDevice());

        attendance = attendanceRepository.save(attendance);

        log.info("User {} checked in at {}", user.getName(), attendance.getCheckInTime());

        return attendanceMapper.toDTO(attendance);
    }

    @Override
    public AttendanceDTO checkOut(CheckOutRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        LocalDate today = LocalDate.now();

        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, today)
                .orElseThrow(() -> new ResourceNotFoundException("No check-in record found for today"));

        if (attendance.getCheckInTime() == null) {
            throw new BadRequestException("Must check in before checking out");
        }

        if (attendance.getCheckOutTime() != null) {
            throw new BadRequestException("Already checked out today");
        }

        // Check out
        attendance.checkOut(
                LocalTime.now(),
                request.getLocation(),
                request.getIpAddress(),
                request.getDevice());

        attendance = attendanceRepository.save(attendance);

        log.info("User {} checked out at {}", user.getName(), attendance.getCheckOutTime());

        return attendanceMapper.toDTO(attendance);
    }

    @Override
    public AttendanceDTO requestLeave(LeaveRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if attendance already exists for the date
        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndDate(
                userId, request.getDate());

        if (existingAttendance.isPresent()) {
            throw new BadRequestException("Attendance record already exists for this date");
        }

        // Create leave request
        Attendance attendance = Attendance.builder()
                .user(user)
                .date(request.getDate())
                .status(AttendanceStatus.LEAVE)
                .leaveType(request.getLeaveType())
                .leaveReason(request.getReason())
                .notes(request.getNotes())
                .build();

        attendance = attendanceRepository.save(attendance);

        log.info("User {} requested leave for {}", user.getName(), request.getDate());

        return attendanceMapper.toDTO(attendance);
    }

    @Override
    public void approveLeave(Long attendanceId, Long approvedBy) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        if (attendance.getStatus() != AttendanceStatus.LEAVE) {
            throw new BadRequestException("Can only approve leave requests");
        }

        if (attendance.getIsApproved() != null) {
            throw new BadRequestException("Leave request already processed");
        }

        attendance.approve(approvedBy);
        attendanceRepository.save(attendance);

        // Send notification to user
        notificationService.sendNotification(
                attendance.getUser().getId(),
                com.hoangthanhhong.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                "Leave Approved",
                String.format("Your leave request for %s has been approved", attendance.getDate()),
                java.util.Map.of("attendanceId", attendanceId));

        log.info("Leave approved for user {} on {}", attendance.getUser().getName(), attendance.getDate());
    }

    @Override
    public void rejectLeave(Long attendanceId, Long rejectedBy, String reason) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        if (attendance.getStatus() != AttendanceStatus.LEAVE) {
            throw new BadRequestException("Can only reject leave requests");
        }

        if (attendance.getIsApproved() != null) {
            throw new BadRequestException("Leave request already processed");
        }

        attendance.setIsApproved(false);
        attendance.setApprovedBy(rejectedBy);
        attendance.setApprovedAt(java.time.LocalDateTime.now());
        attendance.setAdminNotes(reason);
        attendance.setStatus(AttendanceStatus.ABSENT);

        attendanceRepository.save(attendance);

        // Send notification to user
        notificationService.sendNotification(
                attendance.getUser().getId(),
                com.hoangthanhhong.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                "Leave Rejected",
                String.format("Your leave request for %s has been rejected: %s", attendance.getDate(), reason),
                java.util.Map.of("attendanceId", attendanceId));

        log.info("Leave rejected for user {} on {}", attendance.getUser().getName(), attendance.getDate());
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getAttendanceById(Long id) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        return attendanceMapper.toDTO(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceDTO getUserAttendanceForDate(Long userId, LocalDate date) {
        Attendance attendance = attendanceRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        return attendanceMapper.toDTO(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceDTO> getUserAttendance(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendances = attendanceRepository.findByUserIdAndDateRange(
                userId, startDate, endDate);

        return attendances.stream()
                .map(attendanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttendanceDTO> getUserAttendance(
            Long userId, LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Page<Attendance> attendances = attendanceRepository.findByUserIdAndDateRange(
                userId, startDate, endDate, pageable);

        return attendances.map(attendanceMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceStatisticsDTO getUserAttendanceStatistics(
            Long userId, LocalDate startDate, LocalDate endDate) {

        List<Object[]> statusCounts = attendanceRepository.countByStatus(startDate, endDate, userId);
        Double totalWorkHours = attendanceRepository.getTotalWorkHours(userId, startDate, endDate);
        Double totalOvertimeHours = attendanceRepository.getTotalOvertimeHours(userId, startDate, endDate);
        Long presentDays = attendanceRepository.countPresentDays(userId, startDate, endDate);
        Long absentDays = attendanceRepository.countAbsentDays(userId, startDate, endDate);
        Long leaveDays = attendanceRepository.countLeaveDays(userId, startDate, endDate);
        Long lateDays = attendanceRepository.countLateDays(userId, startDate, endDate);
        Double attendanceRate = attendanceRepository.getAttendanceRate(userId, startDate, endDate);

        return AttendanceStatisticsDTO.builder()
                .userId(userId)
                .startDate(startDate)
                .endDate(endDate)
                .totalWorkHours(totalWorkHours)
                .totalOvertimeHours(totalOvertimeHours)
                .presentDays(presentDays.intValue())
                .absentDays(absentDays.intValue())
                .leaveDays(leaveDays.intValue())
                .lateDays(lateDays.intValue())
                .attendanceRate(attendanceRate)
                .statusCounts(statusCounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceStatisticsDTO> getAllUsersAttendanceStatistics(
            LocalDate startDate, LocalDate endDate) {

        List<Object[]> stats = attendanceRepository.getAttendanceStatisticsByUser(startDate, endDate);

        return stats.stream()
                .map(row -> AttendanceStatisticsDTO.builder()
                        .userId((Long) row[0])
                        .userName((String) row[1])
                        .totalDays(((Number) row[2]).intValue())
                        .presentDays(((Number) row[3]).intValue())
                        .absentDays(((Number) row[4]).intValue())
                        .leaveDays(((Number) row[5]).intValue())
                        .lateDays(((Number) row[6]).intValue())
                        .totalWorkHours(((Number) row[7]).doubleValue())
                        .totalOvertimeHours(((Number) row[8]).doubleValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void verifyAttendance(Long attendanceId, Long verifiedBy) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found"));

        attendance.verify(verifiedBy);
        attendanceRepository.save(attendance);

        log.info("Attendance verified for user {} on {}",
                attendance.getUser().getName(), attendance.getDate());
    }

    @Override
    public void markAsAbsent(Long userId, LocalDate date, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndDate(userId, date);

        Attendance attendance;
        if (existingAttendance.isPresent()) {
            attendance = existingAttendance.get();
        } else {
            attendance = Attendance.builder()
                    .user(user)
                    .date(date)
                    .build();
        }

        attendance.markAsAbsent();
        attendance.setAdminNotes(reason);

        attendanceRepository.save(attendance);

        log.info("Marked user {} as absent on {}", user.getName(), date);
    }

    @Override
    public void markAsHoliday(LocalDate date) {
        // Get all active staff/employees
        List<User> users = userRepository.findByRole_NameIn(List.of("STAFF", "MANAGER"));

        for (User user : users) {
            Optional<Attendance> existingAttendance = attendanceRepository.findByUserIdAndDate(
                    user.getId(), date);

            Attendance attendance;
            if (existingAttendance.isPresent()) {
                attendance = existingAttendance.get();
            } else {
                attendance = Attendance.builder()
                        .user(user)
                        .date(date)
                        .build();
            }

            attendance.markAsHoliday();
            attendanceRepository.save(attendance);
        }

        log.info("Marked {} as holiday for all users", date);
    }
}
