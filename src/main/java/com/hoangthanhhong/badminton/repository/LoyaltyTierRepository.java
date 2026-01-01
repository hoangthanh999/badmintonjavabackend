package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {

    Optional<LoyaltyTier> findByLevel(Integer level);

    Optional<LoyaltyTier> findByName(String name);

    @Query("""
                SELECT lt FROM LoyaltyTier lt
                ORDER BY lt.level ASC
            """)
    List<LoyaltyTier> findAllOrderByLevel();

    @Query("""
                SELECT lt FROM LoyaltyTier lt
                WHERE lt.minPoints <= :points
                AND (lt.maxPoints IS NULL OR lt.maxPoints >= :points)
            """)
    Optional<LoyaltyTier> findByPoints(@Param("points") Integer points);

    @Query("""
                SELECT lt FROM LoyaltyTier lt
                WHERE lt.level > :currentLevel
                ORDER BY lt.level ASC
                LIMIT 1
            """)
    Optional<LoyaltyTier> findNextTier(@Param("currentLevel") Integer currentLevel);

    @Query("""
                SELECT COUNT(u)
                FROM User u
                WHERE u.loyaltyTier.id = :tierId
            """)
    Long countUsersByTierId(@Param("tierId") Long tierId);
}
