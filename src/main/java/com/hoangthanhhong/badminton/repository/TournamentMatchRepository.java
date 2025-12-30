package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.TournamentMatch;
import com.hoangthanhhong.badminton.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TournamentMatchRepository extends JpaRepository<TournamentMatch, Long> {

    List<TournamentMatch> findByTournamentId(Long tournamentId);

    List<TournamentMatch> findByRoundId(Long roundId);

    List<TournamentMatch> findByStatus(MatchStatus status);

    @Query("""
                SELECT tm FROM TournamentMatch tm
                WHERE tm.tournament.id = :tournamentId
                AND tm.status = :status
                ORDER BY tm.scheduledTime ASC NULLS LAST
            """)
    List<TournamentMatch> findByTournamentIdAndStatus(
            @Param("tournamentId") Long tournamentId,
            @Param("status") MatchStatus status);

    @Query("""
                SELECT tm FROM TournamentMatch tm
                WHERE (tm.participant1.id = :participantId OR tm.participant2.id = :participantId)
                AND tm.tournament.id = :tournamentId
                ORDER BY tm.scheduledTime ASC
            """)
    List<TournamentMatch> findByTournamentIdAndParticipantId(
            @Param("tournamentId") Long tournamentId,
            @Param("participantId") Long participantId);

    @Query("""
                SELECT tm FROM TournamentMatch tm
                WHERE tm.scheduledTime BETWEEN :startTime AND :endTime
                AND tm.status IN ('SCHEDULED', 'IN_PROGRESS')
                ORDER BY tm.scheduledTime ASC
            """)
    List<TournamentMatch> findScheduledMatches(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    @Query("""
                SELECT tm FROM TournamentMatch tm
                WHERE tm.tournament.id = :tournamentId
                AND tm.status = 'IN_PROGRESS'
            """)
    List<TournamentMatch> findLiveMatches(@Param("tournamentId") Long tournamentId);

    @Query("""
                SELECT COUNT(tm) FROM TournamentMatch tm
                WHERE tm.tournament.id = :tournamentId
                AND tm.status = 'COMPLETED'
            """)
    Long countCompletedMatches(@Param("tournamentId") Long tournamentId);

    @Query("""
                SELECT tm FROM TournamentMatch tm
                WHERE tm.court.id = :courtId
                AND tm.scheduledTime BETWEEN :startTime AND :endTime
                AND tm.status != 'CANCELLED'
                ORDER BY tm.scheduledTime ASC
            """)
    List<TournamentMatch> findByCourtIdAndTimeRange(
            @Param("courtId") Long courtId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
}
