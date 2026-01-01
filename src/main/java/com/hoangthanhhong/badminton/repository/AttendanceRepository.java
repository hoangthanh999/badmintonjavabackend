package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Attendance;
import com.hoangthanhhong.badminton.enums.AttendanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // === BASIC QUERIES ===

    Optional<Attendance> findByUserIdAndDate(Long userId, LocalDate date);

    List<Attendance> findByUserId(Long userId);

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByStatus(AttendanceStatus status);

    // === COMPLEX QUERIES ===

    // 1. Tìm attendance của user trong khoảng thời gian
    @Query("""
                SELECT a FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
                ORDER BY a.date DESC
            """)
    List<Attendance> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
                SELECT a FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
                ORDER BY a.date DESC
            """)
    Page<Attendance> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    // 2. Tìm attendance theo status trong khoảng thời gian
    @Query("""
                SELECT a FROM Attendance a
                WHERE a.status = :status
                AND a.date BETWEEN :startDate AND :endDate
                ORDER BY a.date DESC
            """)
    List<Attendance> findByStatusAndDateRange(
            @Param("status") AttendanceStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 3. Tìm attendance chưa verify
    @Query("""
                SELECT a FROM Attendance a
                WHERE a.isVerified = false
                AND a.status = 'PRESENT'
                AND a.date BETWEEN :startDate AND :endDate
                ORDER BY a.date DESC
            """)
    List<Attendance> findUnverifiedAttendance(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 4. Tìm attendance đi muộn
    @Query("""
                SELECT a FROM Attendance a
                WHERE a.isLate = true
                AND a.date BETWEEN :startDate AND :endDate
                AND (:userId IS NULL OR a.user.id = :userId)
                ORDER BY a.date DESC
            """)
    List<Attendance> findLateAttendance(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId);

    // 5. Tìm attendance về sớm
    @Query("""
                SELECT a FROM Attendance a
                WHERE a.isEarlyDeparture = true
                AND a.date BETWEEN :startDate AND :endDate
                AND (:userId IS NULL OR a.user.id = :userId)
                ORDER BY a.date DESC
            """)
    List<Attendance> findEarlyDepartureAttendance(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId);

    // 6. Tìm leave requests cần approval
    @Query("""
                SELECT a FROM Attendance a
                WHERE a.status = 'LEAVE'
                AND a.isApproved IS NULL
                AND a.date >= :currentDate
                ORDER BY a.date ASC
            """)
    List<Attendance> findPendingLeaveRequests(@Param("currentDate") LocalDate currentDate);

    // 7. Đếm attendance theo status
    @Query("""
                SELECT a.status, COUNT(a)
                FROM Attendance a
                WHERE a.date BETWEEN :startDate AND :endDate
                AND (:userId IS NULL OR a.user.id = :userId)
                GROUP BY a.status
            """)
    List<Object[]> countByStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("userId") Long userId);

    // 8. Tính tổng giờ làm việc
    @Query("""
                SELECT COALESCE(SUM(a.workHours), 0)
                FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
                AND a.status = 'PRESENT'
            """)
    Double getTotalWorkHours(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 9. Tính tổng giờ overtime
    @Query("""
                SELECT COALESCE(SUM(a.overtimeHours), 0)
                FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
                AND a.overtimeHours IS NOT NULL
            """)
    Double getTotalOvertimeHours(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 10. Đếm số ngày present
    @Query("""
                SELECT COUNT(a)
                FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
                AND a.status = 'PRESENT'
            """)
    Long countPresentDays(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 11. Đếm số ngày absent
    @Query("""
                SELECT COUNT(a)
                FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
                AND a.status = 'ABSENT'
            """)
    Long countAbsentDays(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 12. Đếm số ngày leave
    @Query("""
                SELECT COUNT(a)
                FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
                AND a.status = 'LEAVE'
            """)
    Long countLeaveDays(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 13. Đếm số lần đi muộn
    @Query("""
                SELECT COUNT(a)
                FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
                AND a.isLate = true
            """)
    Long countLateDays(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 14. Thống kê attendance theo user
    @Query("""
                SELECT
                    a.user.id,
                    a.user.name,
                    COUNT(a) as totalDays,
                    SUM(CASE WHEN a.status = 'PRESENT' THEN 1 ELSE 0 END) as presentDays,
                    SUM(CASE WHEN a.status = 'ABSENT' THEN 1 ELSE 0 END) as absentDays,
                    SUM(CASE WHEN a.status = 'LEAVE' THEN 1 ELSE 0 END) as leaveDays,
                    SUM(CASE WHEN a.isLate = true THEN 1 ELSE 0 END) as lateDays,
                    SUM(a.workHours) as totalWorkHours,
                    SUM(a.overtimeHours) as totalOvertimeHours
                FROM Attendance a
                WHERE a.date BETWEEN :startDate AND :endDate
                GROUP BY a.user.id, a.user.name
                ORDER BY a.user.name
            """)
    List<Object[]> getAttendanceStatisticsByUser(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 15. Tìm users chưa check-in hôm nay
    @Query("""
                SELECT u FROM User u
                WHERE u.role.name IN ('STAFF', 'MANAGER')
                AND u.isActive = true
                AND NOT EXISTS (
                    SELECT 1 FROM Attendance a
                    WHERE a.user.id = u.id
                    AND a.date = :date
                )
            """)
    List<com.hoangthanhhong.badminton.entity.User> findUsersWithoutAttendance(@Param("date") LocalDate date);

    // 16. Tính attendance rate
    @Query("""
                SELECT
                    (CAST(COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) AS double) / COUNT(a)) * 100
                FROM Attendance a
                WHERE a.user.id = :userId
                AND a.date BETWEEN :startDate AND :endDate
            """)
    Double getAttendanceRate(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
