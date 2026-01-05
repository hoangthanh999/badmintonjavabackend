package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Court;
import com.hoangthanhhong.badminton.enums.CourtStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    List<Court> findByStatus(CourtStatus status);

    Page<Court> findByStatus(CourtStatus status, Pageable pageable);

    @Query("SELECT c FROM Court c WHERE c.name LIKE %:keyword% OR c.location LIKE %:keyword%")
    Page<Court> searchCourts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT c FROM Court c WHERE c.status = :status AND c.id NOT IN " +
            "(SELECT b.court.id FROM Booking b WHERE b.status = 'CONFIRMED' " +
            "AND ((b.startTime <= :endTime AND b.endTime >= :startTime)))")
    List<Court> findAvailableCourts(
            @Param("status") CourtStatus status,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    Optional<Court> findByName(String name);

    long countByStatus(CourtStatus status);
}
