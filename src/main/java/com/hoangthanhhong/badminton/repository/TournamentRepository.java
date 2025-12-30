package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Tournament;
import com.hoangthanhhong.badminton.enums.TournamentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    // === BASIC QUERIES ===

    List<Tournament> findByStatus(TournamentStatus status);

    Page<Tournament> findByStatus(TournamentStatus status, Pageable pageable);

    List<Tournament> findByCourtId(Long courtId);

    // === COMPLEX QUERIES ===

    // 1. Tìm tournament đang mở đăng ký
    @Query("""
                SELECT t FROM Tournament t
                WHERE t.status = 'UPCOMING'
                AND (t.registrationStartDate IS NULL OR t.registrationStartDate <= :currentDate)
                AND (t.registrationEndDate IS NULL OR t.registrationEndDate >= :currentDate)
                AND t.currentParticipants < t.maxParticipants
                AND t.deletedAt IS NULL
                ORDER BY t.startDate ASC
            """)
    List<Tournament> findOpenForRegistration(@Param("currentDate") LocalDate currentDate);

    // 2. Tìm tournament sắp diễn ra
    @Query("""
                SELECT t FROM Tournament t
                WHERE t.status = 'UPCOMING'
                AND t.startDate BETWEEN :startDate AND :endDate
                AND t.deletedAt IS NULL
                ORDER BY t.startDate ASC
            """)
    List<Tournament> findUpcomingTournaments(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 3. Tìm tournament đang diễn ra
    @Query("""
                SELECT t FROM Tournament t
                WHERE t.status = 'ONGOING'
                AND t.deletedAt IS NULL
                ORDER BY t.startedAt DESC
            """)
    List<Tournament> findOngoingTournaments();

    // 4. Tìm tournament theo khoảng thời gian
    @Query("""
                SELECT t FROM Tournament t
                WHERE (
                    (t.startDate BETWEEN :startDate AND :endDate)
                    OR (t.endDate BETWEEN :startDate AND :endDate)
                    OR (t.startDate <= :startDate AND t.endDate >= :endDate)
                )
                AND (:status IS NULL OR t.status = :status)
                AND t.deletedAt IS NULL
                ORDER BY t.startDate ASC
            """)
    List<Tournament> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") TournamentStatus status);

    // 5. Tìm tournament public
    @Query("""
                SELECT t FROM Tournament t
                WHERE t.isPublic = true
                AND t.status IN ('UPCOMING', 'ONGOING')
                AND t.deletedAt IS NULL
                ORDER BY t.startDate ASC
            """)
    Page<Tournament> findPublicTournaments(Pageable pageable);

    // 6. Tìm tournament featured
    @Query("""
                SELECT t FROM Tournament t
                WHERE t.isFeatured = true
                AND t.status IN ('UPCOMING', 'ONGOING')
                AND t.deletedAt IS NULL
                ORDER BY t.startDate ASC
            """)
    List<Tournament> findFeaturedTournaments(Pageable pageable);

    // 7. Tìm tournament theo user tham gia
    @Query("""
                SELECT DISTINCT t FROM Tournament t
                JOIN t.participants p
                WHERE p.user.id = :userId
                AND t.deletedAt IS NULL
                ORDER BY t.startDate DESC
            """)
    List<Tournament> findByParticipantUserId(@Param("userId") Long userId);

    // 8. Tìm tournament có thể tham gia
    @Query("""
                SELECT t FROM Tournament t
                WHERE t.status = 'UPCOMING'
                AND (t.registrationStartDate IS NULL OR t.registrationStartDate <= :currentDate)
                AND (t.registrationEndDate IS NULL OR t.registrationEndDate >= :currentDate)
                AND t.currentParticipants < t.maxParticipants
                AND t.isPublic = true
                AND NOT EXISTS (
                    SELECT 1 FROM t.participants p
                    WHERE p.user.id = :userId
                )
                AND t.deletedAt IS NULL
                ORDER BY t.startDate ASC
            """)
    List<Tournament> findAvailableForUser(
            @Param("userId") Long userId,
            @Param("currentDate") LocalDate currentDate);

    // 9. Thống kê tournament
    @Query("""
                SELECT
                    FUNCTION('YEAR', t.startDate) as year,
                    FUNCTION('MONTH', t.startDate) as month,
                    COUNT(t) as totalTournaments,
                    SUM(t.currentParticipants) as totalParticipants,
                    SUM(t.prizePool) as totalPrizePool
                FROM Tournament t
                WHERE t.startDate BETWEEN :startDate AND :endDate
                AND t.deletedAt IS NULL
                GROUP BY FUNCTION('YEAR', t.startDate), FUNCTION('MONTH', t.startDate)
                ORDER BY year DESC, month DESC
            """)
    List<Object[]> getTournamentStatistics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 10. Tìm kiếm tournament
    @Query("""
                SELECT t FROM Tournament t
                WHERE (
                    LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(t.location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
                AND (:status IS NULL OR t.status = :status)
                AND t.deletedAt IS NULL
                ORDER BY t.startDate DESC
            """)
    Page<Tournament> searchTournaments(
            @Param("searchTerm") String searchTerm,
            @Param("status") TournamentStatus status,
            Pageable pageable);

    // 11. Đếm tournament theo status
    @Query("""
                SELECT t.status, COUNT(t)
                FROM Tournament t
                WHERE t.startDate BETWEEN :startDate AND :endDate
                AND t.deletedAt IS NULL
                GROUP BY t.status
            """)
    List<Object[]> countByStatus(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    // 12. Tournament cần bắt đầu
    @Query("""
                SELECT t FROM Tournament t
                WHERE t.status = 'UPCOMING'
                AND t.startDate <= :currentDate
                AND t.currentParticipants >= COALESCE(t.minParticipants, 2)
                AND t.deletedAt IS NULL
            """)
    List<Tournament> findReadyToStart(@Param("currentDate") LocalDate currentDate);
}
