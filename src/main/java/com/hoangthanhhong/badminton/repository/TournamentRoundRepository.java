package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.TournamentRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TournamentRoundRepository extends JpaRepository<TournamentRound, Long> {

    List<TournamentRound> findByTournamentId(Long tournamentId);

    @Query("""
                SELECT tr FROM TournamentRound tr
                WHERE tr.tournament.id = :tournamentId
                ORDER BY tr.roundNumber ASC
            """)
    List<TournamentRound> findByTournamentIdOrderByRoundNumber(@Param("tournamentId") Long tournamentId);

    Optional<TournamentRound> findByTournamentIdAndRoundNumber(Long tournamentId, Integer roundNumber);

    @Query("""
                SELECT tr FROM TournamentRound tr
                WHERE tr.tournament.id = :tournamentId
                AND tr.isCompleted = false
                ORDER BY tr.roundNumber ASC
            """)
    Optional<TournamentRound> findCurrentRound(@Param("tournamentId") Long tournamentId);

    @Query("""
                SELECT tr FROM TournamentRound tr
                WHERE tr.tournament.id = :tournamentId
                ORDER BY tr.roundNumber DESC
                LIMIT 1
            """)
    Optional<TournamentRound> findLastRound(@Param("tournamentId") Long tournamentId);
}
