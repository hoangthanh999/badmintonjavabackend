package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.tournament.*;
import com.hoangthanhhong.badminton.entity.*;
import org.springframework.stereotype.Component;

@Component
public class TournamentMapper {

    public TournamentDTO toDTO(Tournament tournament) {
        if (tournament == null)
            return null;

        return TournamentDTO.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .description(tournament.getDescription())
                .courtId(tournament.getCourt() != null ? tournament.getCourt().getId() : null)
                .courtName(tournament.getCourt() != null ? tournament.getCourt().getName() : null)
                .tournamentType(tournament.getTournamentType())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                .registrationStartDate(tournament.getRegistrationStartDate())
                .registrationEndDate(tournament.getRegistrationEndDate())
                .status(tournament.getStatus())
                .maxParticipants(tournament.getMaxParticipants())
                .currentParticipants(tournament.getCurrentParticipants())
                .minParticipants(tournament.getMinParticipants())
                .entryFee(tournament.getEntryFee())
                .prizePool(tournament.getPrizePool())
                .imageUrl(tournament.getImageUrl())
                .location(tournament.getLocation())
                .isPublic(tournament.getIsPublic())
                .isFeatured(tournament.getIsFeatured())
                .createdAt(tournament.getCreatedAt())
                .build();
    }

    public TournamentDTO toDTOWithDetails(Tournament tournament) {
        return toDTO(tournament);
    }

    public TournamentParticipantDTO toParticipantDTO(TournamentParticipant participant) {
        if (participant == null)
            return null;

        return TournamentParticipantDTO.builder()
                .id(participant.getId())
                .tournamentId(participant.getTournament().getId())
                .userId(participant.getUser().getId())
                .userName(participant.getUser().getName())
                .teamName(participant.getTeamName())
                .status(participant.getStatus())
                .seedNumber(participant.getSeedNumber())
                .matchesPlayed(participant.getMatchesPlayed())
                .matchesWon(participant.getMatchesWon())
                .matchesLost(participant.getMatchesLost())
                .winRate(participant.getWinRate())
                .finalRank(participant.getFinalRank())
                .registeredAt(participant.getRegisteredAt())
                .build();
    }

    public TournamentMatchDTO toMatchDTO(TournamentMatch match) {
        if (match == null)
            return null;

        return TournamentMatchDTO.builder()
                .id(match.getId())
                .tournamentId(match.getTournament().getId())
                .matchNumber(match.getMatchNumber())
                .participant1Id(match.getParticipant1() != null ? match.getParticipant1().getId() : null)
                .participant1Name(match.getParticipant1() != null ? match.getParticipant1().getUser().getName() : null)
                .participant2Id(match.getParticipant2() != null ? match.getParticipant2().getId() : null)
                .participant2Name(match.getParticipant2() != null ? match.getParticipant2().getUser().getName() : null)
                .participant1Score(match.getParticipant1Score())
                .participant2Score(match.getParticipant2Score())
                .status(match.getStatus())
                .scheduledTime(match.getScheduledTime())
                .winnerId(match.getWinner() != null ? match.getWinner().getId() : null)
                .winnerName(match.getWinner() != null ? match.getWinner().getUser().getName() : null)
                .build();
    }

    public TournamentRoundDTO toRoundDTO(TournamentRound round) {
        if (round == null)
            return null;

        return TournamentRoundDTO.builder()
                .id(round.getId())
                .tournamentId(round.getTournament().getId())
                .roundNumber(round.getRoundNumber())
                .name(round.getName())
                .startDate(round.getStartDate())
                .endDate(round.getEndDate())
                .totalMatches(round.getTotalMatches())
                .completedMatches(round.getCompletedMatches())
                .isCompleted(round.getIsCompleted())
                .build();
    }
}
