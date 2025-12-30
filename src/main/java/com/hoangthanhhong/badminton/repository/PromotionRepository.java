package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Promotion;
import com.hoangthanhhong.badminton.enums.PromotionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    // === BASIC QUERIES ===

    Optional<Promotion> findByCode(String code);

    List<Promotion> findByStatus(PromotionStatus status);

    Page<Promotion> findByStatus(PromotionStatus status, Pageable pageable);

    Optional<Promotion> findByCodeAndStatus(String code, PromotionStatus status);

    // === COMPLEX QUERIES ===

    // 1. Tìm promotion đang active
    @Query("""
                SELECT p FROM Promotion p
                WHERE p.status = 'ACTIVE'
                AND p.startDate <= :currentDate
                AND p.endDate >= :currentDate
                AND (p.maxUsage IS NULL OR p.currentUsage < p.maxUsage)
                AND p.deletedAt IS NULL
                ORDER BY p.discountValue DESC
            """)
    List<Promotion> findActivePromotions(@Param("currentDate") LocalDate currentDate);

    // 2. Tìm promotion có thể sử dụng cho user
    @Query("""
                SELECT p FROM Promotion p
                WHERE p.status = 'ACTIVE'
                AND p.startDate <= :currentDate
                AND p.endDate >= :currentDate
                AND (p.maxUsage IS NULL OR p.currentUsage < p.maxUsage)
                AND p.deletedAt IS NULL
                AND (
                    p.isPublic = true
                    OR EXISTS (
                        SELECT 1 FROM p.eligibleUsers u
                        WHERE u.id = :userId
                    )
                )
                AND (
                    p.maxUsagePerUser IS NULL
                    OR (
                        SELECT COUNT(pu) FROM PromotionUsage pu
                        WHERE pu.promotion.id = p.id
                        AND pu.user.id = :userId
                    ) < p.maxUsagePerUser
                )
                ORDER BY p.discountValue DESC
            """)
    List<Promotion> findAvailablePromotionsForUser(
            @Param("userId") Long userId,
            @Param("currentDate") LocalDate currentDate);

    // 3. Tìm promotion theo loại áp dụng
    @Query("""
                SELECT p FROM Promotion p
                WHERE p.status = 'ACTIVE'
                AND p.startDate <= :currentDate
                AND p.endDate >= :currentDate
                AND p.applicableTo = :applicableTo
                AND p.deletedAt IS NULL
            """)
    List<Promotion> findByApplicableTo(
            @Param("applicableTo") String applicableTo,
            @Param("currentDate") LocalDate currentDate);

    // 4. Tìm promotion auto-apply
    @Query("""
                SELECT p FROM Promotion p
                WHERE p.status = 'ACTIVE'
                AND p.startDate <= :currentDate
                AND p.endDate >= :currentDate
                AND p.isAutoApply = true
                AND (p.maxUsage IS NULL OR p.currentUsage < p.maxUsage)
                AND p.minOrderAmount <= :orderAmount
                AND p.deletedAt IS NULL
                ORDER BY p.discountValue DESC
            """)
    List<Promotion> findAutoApplyPromotions(
            @Param("currentDate") LocalDate currentDate,
            @Param("orderAmount") Double orderAmount);

    // 5. Tìm promotion sắp hết hạn
    @Query("""
                SELECT p FROM Promotion p
                WHERE p.status = 'ACTIVE'
                AND p.endDate BETWEEN :startDate AND :endDate
                AND p.deletedAt IS NULL
                ORDER BY p.endDate ASC
            """)
    List<Promotion> findExpiringPromotions(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 6. Tìm promotion theo sân
    @Query("""
                SELECT DISTINCT p FROM Promotion p
                JOIN p.applicableCourts c
                WHERE c.id = :courtId
                AND p.status = 'ACTIVE'
                AND p.startDate <= :currentDate
                AND p.endDate >= :currentDate
                AND p.deletedAt IS NULL
            """)
    List<Promotion> findByCourtId(
            @Param("courtId") Long courtId,
            @Param("currentDate") LocalDate currentDate);

    // 7. Tìm promotion theo sản phẩm
    @Query("""
                SELECT DISTINCT p FROM Promotion p
                JOIN p.applicableProducts prod
                WHERE prod.id = :productId
                AND p.status = 'ACTIVE'
                AND p.startDate <= :currentDate
                AND p.endDate >= :currentDate
                AND p.deletedAt IS NULL
            """)
    List<Promotion> findByProductId(
            @Param("productId") Long productId,
            @Param("currentDate") LocalDate currentDate);

    // 8. Thống kê promotion
    @Query("""
                SELECT
                    p.id,
                    p.code,
                    p.name,
                    p.currentUsage,
                    p.maxUsage,
                    SUM(pu.discountAmount) as totalDiscount,
                    COUNT(pu) as usageCount
                FROM Promotion p
                LEFT JOIN p.usages pu
                WHERE p.startDate BETWEEN :startDate AND :endDate
                AND p.deletedAt IS NULL
                GROUP BY p.id, p.code, p.name, p.currentUsage, p.maxUsage
                ORDER BY totalDiscount DESC
            """)
    List<Object[]> getPromotionStatistics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 9. Kiểm tra code tồn tại
    boolean existsByCode(String code);

    // 10. Đếm promotion active
    @Query("""
                SELECT COUNT(p) FROM Promotion p
                WHERE p.status = 'ACTIVE'
                AND p.startDate <= :currentDate
                AND p.endDate >= :currentDate
                AND p.deletedAt IS NULL
            """)
    Long countActivePromotions(@Param("currentDate") LocalDate currentDate);

    // 11. Tìm promotion featured
    @Query("""
                SELECT p FROM Promotion p
                WHERE p.status = 'ACTIVE'
                AND p.isPublic = true
                AND p.startDate <= :currentDate
                AND p.endDate >= :currentDate
                AND p.deletedAt IS NULL
                ORDER BY p.discountValue DESC
            """)
    List<Promotion> findFeaturedPromotions(
            @Param("currentDate") LocalDate currentDate,
            Pageable pageable);

    // 12. Tìm kiếm promotion
    @Query("""
                SELECT p FROM Promotion p
                WHERE (
                    LOWER(p.code) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
                AND (:status IS NULL OR p.status = :status)
                AND p.deletedAt IS NULL
                ORDER BY p.createdAt DESC
            """)
    Page<Promotion> searchPromotions(
            @Param("searchTerm") String searchTerm,
            @Param("status") PromotionStatus status,
            Pageable pageable);
}
