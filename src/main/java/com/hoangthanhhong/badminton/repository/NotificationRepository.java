package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Notification;
import com.hoangthanhhong.badminton.enums.NotificationPriority;
import com.hoangthanhhong.badminton.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // === BASIC QUERIES ===

    List<Notification> findByUserId(Long userId);

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    List<Notification> findByType(NotificationType type);

    // === COMPLEX QUERIES ===

    // 1. Tìm notification chưa đọc
    @Query("""
                SELECT n FROM Notification n
                WHERE n.user.id = :userId
                AND n.isRead = false
                AND (n.expiresAt IS NULL OR n.expiresAt > :now)
                AND n.deletedAt IS NULL
                ORDER BY n.priority DESC, n.createdAt DESC
            """)
    List<Notification> findUnreadByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    @Query("""
                SELECT n FROM Notification n
                WHERE n.user.id = :userId
                AND n.isRead = false
                AND (n.expiresAt IS NULL OR n.expiresAt > :now)
                AND n.deletedAt IS NULL
                ORDER BY n.priority DESC, n.createdAt DESC
            """)
    Page<Notification> findUnreadByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            Pageable pageable);

    // 2. Đếm notification chưa đọc
    @Query("""
                SELECT COUNT(n)
                FROM Notification n
                WHERE n.user.id = :userId
                AND n.isRead = false
                AND (n.expiresAt IS NULL OR n.expiresAt > :now)
                AND n.deletedAt IS NULL
            """)
    Long countUnreadByUserId(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    // 3. Tìm notification theo type
    @Query("""
                SELECT n FROM Notification n
                WHERE n.user.id = :userId
                AND n.type = :type
                AND n.deletedAt IS NULL
                ORDER BY n.createdAt DESC
            """)
    Page<Notification> findByUserIdAndType(
            @Param("userId") Long userId,
            @Param("type") NotificationType type,
            Pageable pageable);

    // 4. Tìm notification theo priority
    @Query("""
                SELECT n FROM Notification n
                WHERE n.user.id = :userId
                AND n.priority = :priority
                AND n.isRead = false
                AND n.deletedAt IS NULL
                ORDER BY n.createdAt DESC
            """)
    List<Notification> findByUserIdAndPriority(
            @Param("userId") Long userId,
            @Param("priority") NotificationPriority priority);

    // 5. Tìm notification chưa gửi
    @Query("""
                SELECT n FROM Notification n
                WHERE n.isSent = false
                AND n.deletedAt IS NULL
                ORDER BY n.priority DESC, n.createdAt ASC
            """)
    List<Notification> findUnsent(Pageable pageable);

    // 6. Tìm notification cần gửi email
    @Query("""
                SELECT n FROM Notification n
                WHERE n.sendEmail = true
                AND n.emailSent = false
                AND n.deletedAt IS NULL
                ORDER BY n.priority DESC, n.createdAt ASC
            """)
    List<Notification> findPendingEmailNotifications(Pageable pageable);

    // 7. Tìm notification cần gửi SMS
    @Query("""
                SELECT n FROM Notification n
                WHERE n.sendSms = true
                AND n.smsSent = false
                AND n.deletedAt IS NULL
                ORDER BY n.priority DESC, n.createdAt ASC
            """)
    List<Notification> findPendingSmsNotifications(Pageable pageable);

    // 8. Tìm notification cần gửi push
    @Query("""
                SELECT n FROM Notification n
                WHERE n.sendPush = true
                AND n.pushSent = false
                AND n.deletedAt IS NULL
                ORDER BY n.priority DESC, n.createdAt ASC
            """)
    List<Notification> findPendingPushNotifications(Pageable pageable);

    // 9. Đánh dấu tất cả là đã đọc
    @Modifying
    @Query("""
                UPDATE Notification n
                SET n.isRead = true, n.readAt = :readAt
                WHERE n.user.id = :userId
                AND n.isRead = false
            """)
    void markAllAsReadByUserId(
            @Param("userId") Long userId,
            @Param("readAt") LocalDateTime readAt);

    // 10. Xóa notification đã đọc cũ
    @Modifying
    @Query("""
                UPDATE Notification n
                SET n.deletedAt = :now
                WHERE n.user.id = :userId
                AND n.isRead = true
                AND n.readAt < :before
            """)
    void deleteOldReadNotifications(
            @Param("userId") Long userId,
            @Param("before") LocalDateTime before,
            @Param("now") LocalDateTime now);

    // 11. Xóa notification hết hạn
    @Modifying
    @Query("""
                UPDATE Notification n
                SET n.deletedAt = :now
                WHERE n.expiresAt < :now
                AND n.deletedAt IS NULL
            """)
    void deleteExpiredNotifications(@Param("now") LocalDateTime now);

    // 12. Tìm notification theo related entity
    @Query("""
                SELECT n FROM Notification n
                WHERE n.relatedEntityType = :entityType
                AND n.relatedEntityId = :entityId
                AND n.deletedAt IS NULL
                ORDER BY n.createdAt DESC
            """)
    List<Notification> findByRelatedEntity(
            @Param("entityType") String entityType,
            @Param("entityId") Long entityId);

    // 13. Thống kê notification
    @Query("""
                SELECT
                    n.type,
                    COUNT(n) as total,
                    SUM(CASE WHEN n.isRead = true THEN 1 ELSE 0 END) as readCount,
                    SUM(CASE WHEN n.isRead = false THEN 1 ELSE 0 END) as unreadCount
                FROM Notification n
                WHERE n.user.id = :userId
                AND n.createdAt BETWEEN :startDate AND :endDate
                AND n.deletedAt IS NULL
                GROUP BY n.type
            """)
    List<Object[]> getNotificationStatistics(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
