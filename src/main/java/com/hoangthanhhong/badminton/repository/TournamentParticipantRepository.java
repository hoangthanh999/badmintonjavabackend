package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.TournamentParticipant;
import com.hoangthanhhong.badminton.enums.ParticipantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentParticipantRepository extends JpaRepository<TournamentParticipant, Long> {

    List<TournamentParticipant> findByTournamentId(Long tournamentId);

    List<TournamentParticipant> findByUserId(Long userId);

    Optional<TournamentParticipant> findByTournamentIdAndUserId(Long tournamentId, Long userId);

    @Query("""
                SELECT tp FROM TournamentParticipant tp
                WHERE tp.tournament.id = :tournamentId
                AND tp.status = :status
                ORDER BY tp.seedNumber ASC NULLS LAST, tp.registeredAt ASC
            """)
    List<TournamentParticipant> findByTournamentIdAndStatus(
            @Param("tournamentId") Long tournamentId,
            @Param("status") ParticipantStatus status);

    @Query("""
                SELECT COUNT(tp) FROM TournamentParticipant tp
                WHERE tp.tournament.id = :tournamentId
                AND tp.status IN ('REGISTERED', 'CONFIRMED', 'CHECKED_IN')
            """)
    Long countActiveParticipants(@Param("tournamentId") Long tournamentId);

    @Query("""
                SELECT CASE WHEN COUNT(tp) > 0 THEN true ELSE false END
                FROM TournamentParticipant tp
                WHERE tp.tournament.id = :tournamentId
                AND tp.user.id = :userId
                AND tp.status IN ('REGISTERED', 'CONFIRMED', 'CHECKED_IN')
            """)
    boolean existsByTournamentIdAndUserId(
            @Param("tournamentId") Long tournamentId,
            @Param("userId") Long userId);

    // Leaderboard
    @Query("""
                SELECT tp FROM TournamentParticipant tp
                WHERE tp.tournament.id = :tournamentId
                AND tp.status IN ('CONFIRMED', 'CHECKED_IN')
                ORDER BY
                    tp.matchesWon DESC,
                    (tp.pointsScored - tp.pointsConceded) DESC,
                    tp.pointsScored DESC
            """)
    List<TournamentParticipant> getLeaderboard(@Param("tournamentId") Long tournamentId);

    // Top performers
    @Query("""
                SELECT tp FROM TournamentParticipant tp
                WHERE tp.tournament.id = :tournamentId
                AND tp.matchesPlayed > 0
                ORDER BY
                    (CAST(tp.matchesWon AS double) / tp.matchesPlayed) DESC,
                    tp.matchesWon DESC
            """)
    List<TournamentParticipant> getTopPerformers(
            @Param("tournamentId") Long tournamentId,
            org.springframework.data.domain.Pageable pageable);
}
