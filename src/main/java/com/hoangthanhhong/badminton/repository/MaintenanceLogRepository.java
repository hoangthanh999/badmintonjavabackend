package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MaintenanceLogRepository extends JpaRepository<MaintenanceLog, Long> {

    List<MaintenanceLog> findByMaintenanceId(Long maintenanceId);

    @Query("""
                SELECT ml FROM MaintenanceLog ml
                WHERE ml.maintenance.id = :maintenanceId
                ORDER BY ml.performedAt DESC
            """)
    List<MaintenanceLog> findByMaintenanceIdOrderByPerformedAtDesc(@Param("maintenanceId") Long maintenanceId);

    @Query("""
                SELECT ml FROM MaintenanceLog ml
                WHERE ml.performedBy = :userId
                AND ml.performedAt BETWEEN :startDate AND :endDate
                ORDER BY ml.performedAt DESC
            """)
    List<MaintenanceLog> findByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
