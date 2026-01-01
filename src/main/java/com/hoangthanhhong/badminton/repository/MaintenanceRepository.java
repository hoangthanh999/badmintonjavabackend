package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Maintenance;
import com.hoangthanhhong.badminton.enums.MaintenanceStatus;
import com.hoangthanhhong.badminton.enums.MaintenanceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Long> {

    // === BASIC QUERIES ===

    List<Maintenance> findByCourtId(Long courtId);

    Page<Maintenance> findByCourtId(Long courtId, Pageable pageable);

    List<Maintenance> findByStatus(MaintenanceStatus status);

    Page<Maintenance> findByStatus(MaintenanceStatus status, Pageable pageable);

    List<Maintenance> findByType(MaintenanceType type);

    // === COMPLEX QUERIES ===

    // 1. Tìm maintenance theo court và status
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.court.id = :courtId
                AND m.status = :status
                AND m.deletedAt IS NULL
                ORDER BY m.scheduledDate DESC
            """)
    List<Maintenance> findByCourtIdAndStatus(
            @Param("courtId") Long courtId,
            @Param("status") MaintenanceStatus status);

    // 2. Tìm maintenance đang diễn ra
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.status = 'IN_PROGRESS'
                AND m.deletedAt IS NULL
                ORDER BY m.actualStartTime DESC
            """)
    List<Maintenance> findOngoingMaintenance();

    // 3. Tìm maintenance scheduled trong khoảng thời gian
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.scheduledDate BETWEEN :startDate AND :endDate
                AND m.status IN ('SCHEDULED', 'IN_PROGRESS')
                AND (:courtId IS NULL OR m.court.id = :courtId)
                AND m.deletedAt IS NULL
                ORDER BY m.scheduledDate ASC, m.scheduledStartTime ASC
            """)
    List<Maintenance> findScheduledMaintenanceInDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("courtId") Long courtId);

    // 4. Tìm maintenance quá hạn
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.status IN ('SCHEDULED', 'POSTPONED')
                AND m.scheduledDate < :currentDate
                AND m.deletedAt IS NULL
                ORDER BY m.scheduledDate ASC
            """)
    List<Maintenance> findOverdueMaintenance(@Param("currentDate") LocalDate currentDate);

    // 5. Tìm maintenance khẩn cấp
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.isEmergency = true
                AND m.status IN ('SCHEDULED', 'IN_PROGRESS')
                AND m.deletedAt IS NULL
                ORDER BY m.scheduledDate ASC
            """)
    List<Maintenance> findEmergencyMaintenance();

    // 6. Tìm maintenance cần approval
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.requiresApproval = true
                AND m.approvedBy IS NULL
                AND m.status = 'SCHEDULED'
                AND m.deletedAt IS NULL
                ORDER BY m.priority DESC, m.scheduledDate ASC
            """)
    List<Maintenance> findMaintenanceRequiringApproval();

    // 7. Tìm maintenance theo assigned user
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.assignedToId = :userId
                AND m.status IN ('SCHEDULED', 'IN_PROGRESS')
                AND m.deletedAt IS NULL
                ORDER BY m.scheduledDate ASC
            """)
    List<Maintenance> findByAssignedToId(@Param("userId") Long userId);

    // 8. Tìm maintenance recurring
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.isRecurring = true
                AND m.status = 'COMPLETED'
                AND m.nextMaintenanceDate <= :date
                AND m.deletedAt IS NULL
            """)
    List<Maintenance> findRecurringMaintenanceDue(@Param("date") LocalDate date);

    // 9. Thống kê maintenance theo court
    @Query("""
                SELECT
                    m.court.id,
                    m.court.name,
                    COUNT(m) as totalMaintenance,
                    SUM(CASE WHEN m.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed,
                    SUM(CASE WHEN m.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as inProgress,
                    SUM(CASE WHEN m.status = 'SCHEDULED' THEN 1 ELSE 0 END) as scheduled,
                    SUM(m.actualCost) as totalCost
                FROM Maintenance m
                WHERE m.scheduledDate BETWEEN :startDate AND :endDate
                AND m.deletedAt IS NULL
                GROUP BY m.court.id, m.court.name
                ORDER BY totalMaintenance DESC
            """)
    List<Object[]> getMaintenanceStatisticsByCourt(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 10. Thống kê maintenance theo type
    @Query("""
                SELECT
                    m.type,
                    COUNT(m) as count,
                    AVG(m.actualCost) as avgCost,
                    SUM(m.actualCost) as totalCost,
                    AVG(m.actualDuration) as avgDuration
                FROM Maintenance m
                WHERE m.scheduledDate BETWEEN :startDate AND :endDate
                AND m.status = 'COMPLETED'
                AND m.deletedAt IS NULL
                GROUP BY m.type
                ORDER BY count DESC
            """)
    List<Object[]> getMaintenanceStatisticsByType(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 11. Tính tổng chi phí maintenance
    @Query("""
                SELECT COALESCE(SUM(m.actualCost), 0)
                FROM Maintenance m
                WHERE m.scheduledDate BETWEEN :startDate AND :endDate
                AND m.status = 'COMPLETED'
                AND (:courtId IS NULL OR m.court.id = :courtId)
                AND m.deletedAt IS NULL
            """)
    Double getTotalMaintenanceCost(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("courtId") Long courtId);

    // 12. Đếm maintenance theo status
    @Query("""
                SELECT m.status, COUNT(m)
                FROM Maintenance m
                WHERE (:courtId IS NULL OR m.court.id = :courtId)
                AND m.deletedAt IS NULL
                GROUP BY m.status
            """)
    List<Object[]> countByStatus(@Param("courtId") Long courtId);

    // 13. Tìm maintenance với chi phí vượt budget
    @Query("""
                SELECT m FROM Maintenance m
                WHERE m.status = 'COMPLETED'
                AND m.actualCost > m.estimatedCost
                AND m.deletedAt IS NULL
                ORDER BY (m.actualCost - m.estimatedCost) DESC
            """)
    List<Maintenance> findOverBudgetMaintenance(Pageable pageable);

    // 14. Tìm kiếm maintenance
    @Query("""
                SELECT m FROM Maintenance m
                WHERE (
                    LOWER(m.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(m.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(m.court.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
                AND (:status IS NULL OR m.status = :status)
                AND (:type IS NULL OR m.type = :type)
                AND m.deletedAt IS NULL
                ORDER BY m.scheduledDate DESC
            """)
    Page<Maintenance> searchMaintenance(
            @Param("searchTerm") String searchTerm,
            @Param("status") MaintenanceStatus status,
            @Param("type") MaintenanceType type,
            Pageable pageable);

    // 15. Kiểm tra court có maintenance trong khoảng thời gian không
    @Query("""
                SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END
                FROM Maintenance m
                WHERE m.court.id = :courtId
                AND m.scheduledDate = :date
                AND m.status IN ('SCHEDULED', 'IN_PROGRESS')
                AND (
                    (m.scheduledStartTime <= :endTime AND m.scheduledEndTime >= :startTime)
                    OR (m.scheduledStartTime IS NULL OR m.scheduledEndTime IS NULL)
                )
                AND m.deletedAt IS NULL
            """)
    boolean hasMaintenanceConflict(
            @Param("courtId") Long courtId,
            @Param("date") LocalDate date,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime);
}
