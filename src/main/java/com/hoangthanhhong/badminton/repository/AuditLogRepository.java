package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // === BASIC QUERIES ===

    List<AuditLog> findByUserId(Long userId);

    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    List<AuditLog> findByAction(String action);

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

    // === COMPLEX QUERIES ===

    // 1. Tìm audit log theo user trong khoảng thời gian
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.userId = :userId
                AND al.timestamp BETWEEN :startDate AND :endDate
                ORDER BY al.timestamp DESC
            """)
    Page<AuditLog> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 2. Tìm audit log theo action
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.action = :action
                AND al.timestamp BETWEEN :startDate AND :endDate
                ORDER BY al.timestamp DESC
            """)
    Page<AuditLog> findByActionAndDateRange(
            @Param("action") String action,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 3. Tìm audit log theo entity
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.entityType = :entityType
                AND al.entityId = :entityId
                ORDER BY al.timestamp DESC
            """)
    List<AuditLog> findByEntity(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId);

    // 4. Tìm audit log theo IP address
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.ipAddress = :ipAddress
                AND al.timestamp BETWEEN :startDate AND :endDate
                ORDER BY al.timestamp DESC
            """)
    List<AuditLog> findByIpAddress(
            @Param("ipAddress") String ipAddress,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 5. Tìm audit log failed
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.status IN ('FAILURE', 'ERROR')
                AND al.timestamp BETWEEN :startDate AND :endDate
                ORDER BY al.timestamp DESC
            """)
    Page<AuditLog> findFailedActions(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 6. Tìm audit log flagged
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.isFlagged = true
                AND al.timestamp BETWEEN :startDate AND :endDate
                ORDER BY al.timestamp DESC
            """)
    Page<AuditLog> findFlaggedLogs(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 7. Tìm audit log sensitive
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.isSensitive = true
                AND al.timestamp BETWEEN :startDate AND :endDate
                ORDER BY al.timestamp DESC
            """)
    Page<AuditLog> findSensitiveLogs(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 8. Đếm actions theo user
    @Query("""
                SELECT al.action, COUNT(al)
                FROM AuditLog al
                WHERE al.userId = :userId
                AND al.timestamp BETWEEN :startDate AND :endDate
                GROUP BY al.action
                ORDER BY COUNT(al) DESC
            """)
    List<Object[]> countActionsByUser(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 9. Đếm actions theo category
    @Query("""
                SELECT al.actionCategory, COUNT(al)
                FROM AuditLog al
                WHERE al.timestamp BETWEEN :startDate AND :endDate
                GROUP BY al.actionCategory
                ORDER BY COUNT(al) DESC
            """)
    List<Object[]> countActionsByCategory(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 10. Thống kê user activity
    @Query("""
                SELECT
                    al.userId,
                    al.username,
                    COUNT(al) as totalActions,
                    COUNT(DISTINCT al.actionCategory) as categoriesUsed,
                    MAX(al.timestamp) as lastActivity
                FROM AuditLog al
                WHERE al.timestamp BETWEEN :startDate AND :endDate
                GROUP BY al.userId, al.username
                ORDER BY totalActions DESC
            """)
    List<Object[]> getUserActivityStatistics(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 11. Tìm login attempts
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.action = 'LOGIN'
                AND al.userId = :userId
                ORDER BY al.timestamp DESC
            """)
    List<AuditLog> findLoginAttempts(@Param("userId") Long userId, Pageable pageable);

    // 12. Tìm failed login attempts
    @Query("""
                SELECT al FROM AuditLog al
                WHERE al.action = 'LOGIN'
                AND al.status = 'FAILURE'
                AND al.timestamp >= :since
                ORDER BY al.timestamp DESC
            """)
    List<AuditLog> findRecentFailedLogins(@Param("since") LocalDateTime since);

    // 13. Đếm failed login attempts theo IP
    @Query("""
                SELECT COUNT(al)
                FROM AuditLog al
                WHERE al.action = 'LOGIN'
                AND al.status = 'FAILURE'
                AND al.ipAddress = :ipAddress
                AND al.timestamp >= :since
            """)
    Long countFailedLoginsByIp(
            @Param("ipAddress") String ipAddress,
            @Param("since") LocalDateTime since);

    // 14. Tìm kiếm audit logs
    @Query("""
                SELECT al FROM AuditLog al
                WHERE (
                    LOWER(al.username) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(al.action) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(al.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(al.entityType) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
                AND (:action IS NULL OR al.action = :action)
                AND (:status IS NULL OR al.status = :status)
                AND al.timestamp BETWEEN :startDate AND :endDate
                ORDER BY al.timestamp DESC
            """)
    Page<AuditLog> searchAuditLogs(
            @Param("searchTerm") String searchTerm,
            @Param("action") String action,
            @Param("status") String status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 15. Tính average response time
    @Query("""
                SELECT AVG(al.responseTime)
                FROM AuditLog al
                WHERE al.timestamp BETWEEN :startDate AND :endDate
                AND al.responseTime IS NOT NULL
            """)
    Double getAverageResponseTime(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
