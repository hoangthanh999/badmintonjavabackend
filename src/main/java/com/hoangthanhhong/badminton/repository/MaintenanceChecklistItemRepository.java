package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.MaintenanceChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaintenanceChecklistItemRepository extends JpaRepository<MaintenanceChecklistItem, Long> {

    List<MaintenanceChecklistItem> findByMaintenanceId(Long maintenanceId);

    @Query("""
                SELECT mci FROM MaintenanceChecklistItem mci
                WHERE mci.maintenance.id = :maintenanceId
                ORDER BY mci.sortOrder ASC, mci.createdAt ASC
            """)
    List<MaintenanceChecklistItem> findByMaintenanceIdOrdered(@Param("maintenanceId") Long maintenanceId);

    @Query("""
                SELECT COUNT(mci)
                FROM MaintenanceChecklistItem mci
                WHERE mci.maintenance.id = :maintenanceId
                AND mci.isCompleted = true
            """)
    Long countCompletedItems(@Param("maintenanceId") Long maintenanceId);

    @Query("""
                SELECT COUNT(mci)
                FROM MaintenanceChecklistItem mci
                WHERE mci.maintenance.id = :maintenanceId
            """)
    Long countTotalItems(@Param("maintenanceId") Long maintenanceId);
}
