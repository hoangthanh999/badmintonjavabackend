package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.tournament.*;
import com.hoangthanhhong.badminton.dto.request.tournament.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TournamentService {

    // Tournament CRUD
    TournamentDTO createTournament(CreateTournamentRequest request);

    TournamentDTO updateTournament(Long id, UpdateTournamentRequest request);

    void deleteTournament(Long id);

    TournamentDTO getTournamentById(Long id);

    Page<TournamentDTO> getAllTournaments(Pageable pageable);

    List<TournamentDTO> getUpcomingTournaments();

    List<TournamentDTO> getOngoingTournaments();

    List<TournamentDTO> getFeaturedTournaments();

    // Registration
    TournamentParticipantDTO registerForTournament(Long tournamentId, RegisterTournamentRequest request, Long userId);

    void withdrawFromTournament(Long tournamentId, Long userId, String reason);

    void checkInParticipant(Long tournamentId, Long userId);

    // Tournament Management
    void startTournament(Long id);

    void completeTournament(Long id);

    void cancelTournament(Long id, String reason);

    void generateBracket(Long tournamentId);

    // Matches
    TournamentMatchDTO createMatch(CreateMatchRequest request);

    TournamentMatchDTO updateMatchScore(Long matchId, UpdateMatchScoreRequest request);

    void startMatch(Long matchId);

    void completeMatch(Long matchId, Long winnerId);

    void cancelMatch(Long matchId, String reason);

    List<TournamentMatchDTO> getTournamentMatches(Long tournamentId);

    List<TournamentMatchDTO> getLiveMatches(Long tournamentId);

    // Participants
    List<TournamentParticipantDTO> getTournamentParticipants(Long tournamentId);

    List<TournamentParticipantDTO> getLeaderboard(Long tournamentId);

    TournamentParticipantDTO getParticipantById(Long id);

    void updateParticipantSeed(Long participantId, Integer seedNumber);

    void disqualifyParticipant(Long participantId, String reason);

    // Rounds
    TournamentRoundDTO createRound(CreateRoundRequest request);

    List<TournamentRoundDTO> getTournamentRounds(Long tournamentId);

    TournamentRoundDTO getCurrentRound(Long tournamentId);

    // Statistics
    TournamentStatsDTO getTournamentStatistics(Long tournamentId);

    List<TournamentDTO> getUserTournaments(Long userId);

    // Search
    Page<TournamentDTO> searchTournaments(String searchTerm, String status, Pageable pageable);
}
