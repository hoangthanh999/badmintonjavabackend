package com.hoangthanhhong.badminton.scheduler;

import com.hoangthanhhong.badminton.entity.Attendance;
import com.hoangthanhhong.badminton.entity.User;
import com.hoangthanhhong.badminton.enums.AttendanceStatus;
import com.hoangthanhhong.badminton.repository.AttendanceRepository;
import com.hoangthanhhong.badminton.repository.UserRepository;
import com.hoangthanhhong.badminton.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AttendanceScheduler {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    /**
     * Mark users as absent if they haven't checked in by 10:00 AM
     */
    @Scheduled(cron = "0 0 10 * * MON-FRI")
    public void markAbsentUsers() {
        log.info("Marking absent users...");

        LocalDate today = LocalDate.now();
        List<User> usersWithoutAttendance = attendanceRepository.findUsersWithoutAttendance(today);

        int marked = 0;
        for (User user : usersWithoutAttendance) {
            try {
                Attendance attendance = Attendance.builder()
                        .user(user)
                        .date(today)
                        .status(AttendanceStatus.ABSENT)
                        .adminNotes("Auto-marked as absent - No check-in by 10:00 AM")
                        .build();

                attendanceRepository.save(attendance);
                marked++;

                // Send notification
                notificationService.sendNotification(
                        user.getId(),
                        com.hoangthanhhong.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                        "Attendance Alert",
                        "You have been marked as absent for today. Please contact your manager if this is incorrect.",
                        java.util.Map.of("date", today.toString()));

            } catch (Exception e) {
                log.error("Failed to mark user as absent: {}", user.getId(), e);
            }
        }

        log.info("Marked {} users as absent", marked);
    }

    /**
     * Send check-in reminders at 8:30 AM
     */
    @Scheduled(cron = "0 30 8 * * MON-FRI")
    public void sendCheckInReminders() {
        log.info("Sending check-in reminders...");

        LocalDate today = LocalDate.now();
        List<User> usersWithoutAttendance = attendanceRepository.findUsersWithoutAttendance(today);

        for (User user : usersWithoutAttendance) {
            notificationService.sendNotification(
                    user.getId(),
                    com.hoangthanhhong.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                    "Check-In Reminder",
                    "Don't forget to check in for today!",
                    java.util.Map.of("date", today.toString()));
        }

        log.info("Sent {} check-in reminders", usersWithoutAttendance.size());
    }

    /**
     * Send check-out reminders at 5:30 PM
     */
    @Scheduled(cron = "0 30 17 * * MON-FRI")
    public void sendCheckOutReminders() {
        log.info("Sending check-out reminders...");

        LocalDate today = LocalDate.now();

        // Find users who checked in but haven't checked out
        List<Attendance> attendances = attendanceRepository.findByUserIdAndDateRange(
                null, today, today);

        int sent = 0;
        for (Attendance attendance : attendances) {
            if (attendance.getCheckInTime() != null && attendance.getCheckOutTime() == null) {
                notificationService.sendNotification(
                        attendance.getUser().getId(),
                        com.hoangthanhhong.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                        "Check-Out Reminder",
                        "Don't forget to check out before leaving!",
                        java.util.Map.of("date", today.toString()));
                sent++;
            }
        }

        log.info("Sent {} check-out reminders", sent);
    }

    /**
     * Generate monthly attendance report on the 1st of each month
     */
    @Scheduled(cron = "0 0 9 1 * *")
    public void generateMonthlyAttendanceReport() {
        log.info("Generating monthly attendance reports...");

        LocalDate lastMonth = LocalDate.now().minusMonths(1);
        LocalDate startDate = lastMonth.withDayOfMonth(1);
        LocalDate endDate = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());

        List<Object[]> statistics = attendanceRepository.getAttendanceStatisticsByUser(
                startDate, endDate);

        // TODO: Generate and send report to managers

        log.info("Generated monthly attendance report for {} users", statistics.size());
    }
}
