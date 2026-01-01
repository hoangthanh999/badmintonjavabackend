package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.SystemLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

    List<SystemLog> findByLevel(String level);

    List<SystemLog> findByCategory(String category);

    Page<SystemLog> findByLevel(String level, Pageable pageable);

    // 1. Tìm error logs
    @Query("""
                SELECT sl FROM SystemLog sl
                WHERE sl.level IN ('ERROR', 'FATAL')
                AND sl.timestamp BETWEEN :startDate AND :endDate
                ORDER BY sl.timestamp DESC
            """)
    Page<SystemLog> findErrorLogs(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 2. Tìm unresolved errors
    @Query("""
                SELECT sl FROM SystemLog sl
                WHERE sl.level IN ('ERROR', 'FATAL')
                AND sl.isResolved = false
                ORDER BY sl.timestamp DESC
            """)
    List<SystemLog> findUnresolvedErrors();

    // 3. Đếm logs theo level
    @Query("""
                SELECT sl.level, COUNT(sl)
                FROM SystemLog sl
                WHERE sl.timestamp BETWEEN :startDate AND :endDate
                GROUP BY sl.level
                ORDER BY COUNT(sl) DESC
            """)
    List<Object[]> countByLevel(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 4. Đếm logs theo category
    @Query("""
                SELECT sl.category, COUNT(sl)
                FROM SystemLog sl
                WHERE sl.timestamp BETWEEN :startDate AND :endDate
                GROUP BY sl.category
                ORDER BY COUNT(sl) DESC
            """)
    List<Object[]> countByCategory(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 5. Xóa logs cũ
    @Modifying
    @Query("""
                DELETE FROM SystemLog sl
                WHERE sl.timestamp < :before
                AND sl.level NOT IN ('ERROR', 'FATAL')
            """)
    void deleteOldLogs(@Param("before") LocalDateTime before);

    // 6. Tìm kiếm system logs
    @Query("""
                SELECT sl FROM SystemLog sl
                WHERE (
                    LOWER(sl.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(sl.category) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
                AND (:level IS NULL OR sl.level = :level)
                AND sl.timestamp BETWEEN :startDate AND :endDate
                ORDER BY sl.timestamp DESC
            """)
    Page<SystemLog> searchSystemLogs(
            @Param("searchTerm") String searchTerm,
            @Param("level") String level,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);
}
