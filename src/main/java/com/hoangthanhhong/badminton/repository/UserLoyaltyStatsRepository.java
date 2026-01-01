package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.UserLoyaltyStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserLoyaltyStatsRepository extends JpaRepository<UserLoyaltyStats, Long> {

    Optional<UserLoyaltyStats> findByUserId(Long userId);

    @Query("""
                SELECT uls FROM UserLoyaltyStats uls
                ORDER BY uls.currentBalance DESC
            """)
    List<UserLoyaltyStats> findTopUsersByPoints(org.springframework.data.domain.Pageable pageable);

    @Query("""
                SELECT uls FROM UserLoyaltyStats uls
                ORDER BY uls.lifetimePoints DESC
            """)
    List<UserLoyaltyStats> findTopUsersByLifetimePoints(org.springframework.data.domain.Pageable pageable);

    @Query("""
                SELECT uls FROM UserLoyaltyStats uls
                WHERE uls.currentBalance >= :minPoints
                ORDER BY uls.currentBalance DESC
            """)
    List<UserLoyaltyStats> findUsersWithMinimumPoints(@Param("minPoints") Integer minPoints);

    @Query("""
                SELECT
                    COUNT(uls) as totalUsers,
                    SUM(uls.currentBalance) as totalPoints,
                    AVG(uls.currentBalance) as averagePoints,
                    SUM(uls.totalPointsEarned) as totalEarned,
                    SUM(uls.totalPointsSpent) as totalSpent
                FROM UserLoyaltyStats uls
            """)
    Object[] getGlobalLoyaltyStatistics();
}
