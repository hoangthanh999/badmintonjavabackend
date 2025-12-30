package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.PromotionUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PromotionUsageRepository extends JpaRepository<PromotionUsage, Long> {

    List<PromotionUsage> findByPromotionId(Long promotionId);

    List<PromotionUsage> findByUserId(Long userId);

    @Query("""
                SELECT COUNT(pu) FROM PromotionUsage pu
                WHERE pu.promotion.id = :promotionId
                AND pu.user.id = :userId
                AND pu.isReverted = false
            """)
    Long countByPromotionIdAndUserId(
            @Param("promotionId") Long promotionId,
            @Param("userId") Long userId);

    @Query("""
                SELECT pu FROM PromotionUsage pu
                WHERE pu.promotion.id = :promotionId
                AND pu.usedAt BETWEEN :startDate AND :endDate
                AND pu.isReverted = false
            """)
    List<PromotionUsage> findByPromotionIdAndDateRange(
            @Param("promotionId") Long promotionId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
                SELECT
                    SUM(pu.discountAmount) as totalDiscount,
                    COUNT(pu) as usageCount
                FROM PromotionUsage pu
                WHERE pu.promotion.id = :promotionId
                AND pu.isReverted = false
            """)
    Object[] getPromotionUsageSummary(@Param("promotionId") Long promotionId);
}
