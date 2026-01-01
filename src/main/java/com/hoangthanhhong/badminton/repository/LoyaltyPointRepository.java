package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.LoyaltyPoint;
import com.hoangthanhhong.badminton.enums.PointTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoyaltyPointRepository extends JpaRepository<LoyaltyPoint, Long> {

    // === BASIC QUERIES ===

    List<LoyaltyPoint> findByUserId(Long userId);

    Page<LoyaltyPoint> findByUserId(Long userId, Pageable pageable);

    List<LoyaltyPoint> findByTransactionType(PointTransactionType type);

    // === COMPLEX QUERIES ===

    // 1. Tìm transaction của user với filter
    @Query("""
                SELECT lp FROM LoyaltyPoint lp
                WHERE lp.user.id = :userId
                AND (:transactionType IS NULL OR lp.transactionType = :transactionType)
                AND lp.isReversed = false
                AND lp.deletedAt IS NULL
                ORDER BY lp.createdAt DESC
            """)
    Page<LoyaltyPoint> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("transactionType") PointTransactionType transactionType,
            Pageable pageable);

    // 2. Tính tổng điểm của user
    @Query("""
                SELECT COALESCE(SUM(lp.points), 0)
                FROM LoyaltyPoint lp
                WHERE lp.user.id = :userId
                AND lp.isReversed = false
                AND lp.isExpired = false
                AND lp.deletedAt IS NULL
            """)
    Integer getTotalPointsByUserId(@Param("userId") Long userId);

    // 3. Tính điểm đã kiếm được
    @Query("""
                SELECT COALESCE(SUM(lp.points), 0)
                FROM LoyaltyPoint lp
                WHERE lp.user.id = :userId
                AND lp.points > 0
                AND lp.isReversed = false
                AND lp.deletedAt IS NULL
            """)
    Integer getTotalEarnedPointsByUserId(@Param("userId") Long userId);

    // 4. Tính điểm đã sử dụng
    @Query("""
                SELECT COALESCE(SUM(ABS(lp.points)), 0)
                FROM LoyaltyPoint lp
                WHERE lp.user.id = :userId
                AND lp.points < 0
                AND lp.isReversed = false
                AND lp.deletedAt IS NULL
            """)
    Integer getTotalSpentPointsByUserId(@Param("userId") Long userId);

    // 5. Tìm điểm sắp hết hạn
    @Query("""
                SELECT lp FROM LoyaltyPoint lp
                WHERE lp.user.id = :userId
                AND lp.points > 0
                AND lp.expiresAt BETWEEN :now AND :expiryDate
                AND lp.isExpired = false
                AND lp.isReversed = false
                AND lp.deletedAt IS NULL
                ORDER BY lp.expiresAt ASC
            """)
    List<LoyaltyPoint> findExpiringPoints(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            @Param("expiryDate") LocalDateTime expiryDate);

    // 6. Tính điểm sắp hết hạn
    @Query("""
                SELECT COALESCE(SUM(lp.points), 0)
                FROM LoyaltyPoint lp
                WHERE lp.user.id = :userId
                AND lp.points > 0
                AND lp.expiresAt BETWEEN :now AND :expiryDate
                AND lp.isExpired = false
                AND lp.isReversed = false
                AND lp.deletedAt IS NULL
            """)
    Integer getExpiringPointsTotal(
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now,
            @Param("expiryDate") LocalDateTime expiryDate);

    // 7. Tìm điểm đã hết hạn
    @Query("""
                SELECT lp FROM LoyaltyPoint lp
                WHERE lp.expiresAt < :now
                AND lp.isExpired = false
                AND lp.points > 0
                AND lp.isReversed = false
                AND lp.deletedAt IS NULL
            """)
    List<LoyaltyPoint> findExpiredPoints(@Param("now") LocalDateTime now);

    // 8. Tìm transaction theo booking
    List<LoyaltyPoint> findByBookingId(Long bookingId);

    // 9. Tìm transaction theo order
    List<LoyaltyPoint> findByOrderId(Long orderId);

    // 10. Thống kê điểm theo loại transaction
    @Query("""
                SELECT
                    lp.transactionType,
                    COUNT(lp) as transactionCount,
                    SUM(lp.points) as totalPoints
                FROM LoyaltyPoint lp
                WHERE lp.user.id = :userId
                AND lp.createdAt BETWEEN :startDate AND :endDate
                AND lp.isReversed = false
                AND lp.deletedAt IS NULL
                GROUP BY lp.transactionType
                ORDER BY totalPoints DESC
            """)
    List<Object[]> getPointStatisticsByType(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 11. Thống kê điểm theo tháng
    @Query("""
                SELECT
                    FUNCTION('YEAR', lp.createdAt) as year,
                    FUNCTION('MONTH', lp.createdAt) as month,
                    SUM(CASE WHEN lp.points > 0 THEN lp.points ELSE 0 END) as earned,
                    SUM(CASE WHEN lp.points < 0 THEN ABS(lp.points) ELSE 0 END) as spent
                FROM LoyaltyPoint lp
                WHERE lp.user.id = :userId
                AND lp.createdAt BETWEEN :startDate AND :endDate
                AND lp.isReversed = false
                AND lp.deletedAt IS NULL
                GROUP BY FUNCTION('YEAR', lp.createdAt), FUNCTION('MONTH', lp.createdAt)
                ORDER BY year DESC, month DESC
            """)
    List<Object[]> getMonthlyPointStatistics(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 12. Kiểm tra có transaction nào cho entity không
    boolean existsByRelatedEntityTypeAndRelatedEntityId(String entityType, Long entityId);
}
