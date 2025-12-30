package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.tournament.*;
import com.hoangthanhhong.badminton.dto.request.tournament.*;
import com.hoangthanhhong.badminton.entity.*;
import com.hoangthanhhong.badminton.enums.*;
import com.hoangthanhhong.badminton.exception.BadRequestException;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.TournamentMapper;
import com.hoangthanhhong.badminton.repository.*;
import com.hoangthanhhong.badminton.service.TournamentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TournamentServiceImpl implements TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentParticipantRepository participantRepository;
    private final TournamentMatchRepository matchRepository;
    private final TournamentRoundRepository roundRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final TournamentMapper tournamentMapper;
    private final SimpMessagingTemplate messagingTemplate;

    // ===== TOURNAMENT CRUD =====

    @Override
    public TournamentDTO createTournament(CreateTournamentRequest request) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        Court court = null;
        if (request.getCourtId() != null) {
            court = courtRepository.findById(request.getCourtId())
                    .orElseThrow(() -> new ResourceNotFoundException("Court not found"));
        }

        Tournament tournament = Tournament.builder()
                .name(request.getName())
                .description(request.getDescription())
                .court(court)
                .tournamentType(request.getTournamentType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .registrationStartDate(request.getRegistrationStartDate())
                .registrationEndDate(request.getRegistrationEndDate())
                .status(TournamentStatus.UPCOMING)
                .maxParticipants(request.getMaxParticipants())
                .minParticipants(request.getMinParticipants())
                .entryFee(request.getEntryFee())
                .prizePool(request.getPrizePool())
                .firstPrize(request.getFirstPrize())
                .secondPrize(request.getSecondPrize())
                .thirdPrize(request.getThirdPrize())
                .imageUrl(request.getImageUrl())
                .bannerUrl(request.getBannerUrl())
                .rules(request.getRules())
                .format(request.getFormat())
                .ageRestriction(request.getAgeRestriction())
                .skillLevel(request.getSkillLevel())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .isFeatured(request.getIsFeatured() != null ? request.getIsFeatured() : false)
                .organizerId(request.getOrganizerId())
                .organizerName(request.getOrganizerName())
                .sponsor(request.getSponsor())
                .location(request.getLocation())
                .build();

        tournament = tournamentRepository.save(tournament);

        log.info("Created tournament: {}", tournament.getName());
        return tournamentMapper.toDTO(tournament);
    }

    @Override
    public TournamentDTO updateTournament(Long id, UpdateTournamentRequest request) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        if (request.getName() != null) {
            tournament.setName(request.getName());
        }
        if (request.getDescription() != null) {
            tournament.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            tournament.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            tournament.setEndDate(request.getEndDate());
        }
        if (request.getMaxParticipants() != null) {
            tournament.setMaxParticipants(request.getMaxParticipants());
        }
        if (request.getEntryFee() != null) {
            tournament.setEntryFee(request.getEntryFee());
        }
        if (request.getPrizePool() != null) {
            tournament.setPrizePool(request.getPrizePool());
        }
        if (request.getImageUrl() != null) {
            tournament.setImageUrl(request.getImageUrl());
        }
        if (request.getRules() != null) {
            tournament.setRules(request.getRules());
        }
        if (request.getLocation() != null) {
            tournament.setLocation(request.getLocation());
        }

        tournament = tournamentRepository.save(tournament);

        log.info("Updated tournament: {}", tournament.getName());
        return tournamentMapper.toDTO(tournament);
    }

    @Override
    public void deleteTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        if (tournament.getStatus() == TournamentStatus.ONGOING) {
            throw new BadRequestException("Cannot delete ongoing tournament");
        }

        tournament.softDelete();
        tournamentRepository.save(tournament);

        log.info("Deleted tournament: {}", tournament.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentDTO getTournamentById(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        return tournamentMapper.toDTOWithDetails(tournament);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TournamentDTO> getAllTournaments(Pageable pageable) {
        Page<Tournament> tournaments = tournamentRepository.findAll(pageable);
        return tournaments.map(tournamentMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentDTO> getUpcomingTournaments() {
        LocalDate now = LocalDate.now();
        LocalDate endDate = now.plusMonths(3);
        List<Tournament> tournaments = tournamentRepository.findUpcomingTournaments(now, endDate);
        return tournaments.stream()
                .map(tournamentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentDTO> getOngoingTournaments() {
        List<Tournament> tournaments = tournamentRepository.findOngoingTournaments();
        return tournaments.stream()
                .map(tournamentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentDTO> getFeaturedTournaments() {
        List<Tournament> tournaments = tournamentRepository.findFeaturedTournaments(
                org.springframework.data.domain.PageRequest.of(0, 10));
        return tournaments.stream()
                .map(tournamentMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ===== REGISTRATION =====

    @Override
    public TournamentParticipantDTO registerForTournament(
            Long tournamentId,
            RegisterTournamentRequest request,
            Long userId) {

        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate registration
        if (!tournament.isRegistrationOpen()) {
            throw new BadRequestException("Registration is not open for this tournament");
        }

        // Check if already registered
        if (participantRepository.existsByTournamentIdAndUserId(tournamentId, userId)) {
            throw new BadRequestException("Already registered for this tournament");
        }

        // Check if full
        if (tournament.isFull()) {
            throw new BadRequestException("Tournament is full");
        }

        // Create participant
        TournamentParticipant participant = TournamentParticipant.builder()
                .tournament(tournament)
                .user(user)
                .status(ParticipantStatus.REGISTERED)
                .teamName(request.getTeamName())
                .jerseyNumber(request.getJerseyNumber())
                .notes(request.getNotes())
                .build();

        // Handle partner for doubles
        if (tournament.getTournamentType() == TournamentType.DOUBLES ||
                tournament.getTournamentType() == TournamentType.MIXED_DOUBLES) {

            if (request.getPartnerId() == null) {
                throw new BadRequestException("Partner is required for doubles tournament");
            }

            User partner = userRepository.findById(request.getPartnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Partner not found"));

            participant.setPartner(partner);
        }

        participant = participantRepository.save(participant);

        // Update tournament participant count
        tournament.incrementParticipants();
        tournamentRepository.save(tournament);

        // Send notification via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/tournament/" + tournamentId + "/registration",
                tournamentMapper.toParticipantDTO(participant));

        log.info("User {} registered for tournament {}", userId, tournamentId);
        return tournamentMapper.toParticipantDTO(participant);
    }

    @Override
    public void withdrawFromTournament(Long tournamentId, Long userId, String reason) {
        TournamentParticipant participant = participantRepository
                .findByTournamentIdAndUserId(tournamentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        Tournament tournament = participant.getTournament();

        if (tournament.getStatus() == TournamentStatus.ONGOING) {
            throw new BadRequestException("Cannot withdraw from ongoing tournament");
        }

        participant.withdraw(reason);
        participantRepository.save(participant);

        // Update tournament participant count
        tournament.decrementParticipants();
        tournamentRepository.save(tournament);

        log.info("User {} withdrew from tournament {}", userId, tournamentId);
    }

    @Override
    public void checkInParticipant(Long tournamentId, Long userId) {
        TournamentParticipant participant = participantRepository
                .findByTournamentIdAndUserId(tournamentId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.checkIn();
        participantRepository.save(participant);

        log.info("User {} checked in for tournament {}", userId, tournamentId);
    }

    // ===== TOURNAMENT MANAGEMENT =====

    @Override
    public void startTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        if (!tournament.canStart()) {
            throw new BadRequestException("Tournament cannot be started yet");
        }

        tournament.start();
        tournamentRepository.save(tournament);

        // Generate initial bracket
        generateBracket(id);

        // Broadcast tournament started
        messagingTemplate.convertAndSend(
                "/topic/tournament/" + id + "/status",
                Map.of("status", "STARTED", "message", "Tournament has started!"));

        log.info("Started tournament: {}", tournament.getName());
    }

    @Override
    public void completeTournament(Long id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        if (tournament.getStatus() != TournamentStatus.ONGOING) {
            throw new BadRequestException("Only ongoing tournaments can be completed");
        }

        tournament.complete();
        tournamentRepository.save(tournament);

        // Broadcast tournament completed
        messagingTemplate.convertAndSend(
                "/topic/tournament/" + id + "/status",
                Map.of("status", "COMPLETED", "message", "Tournament has completed!"));

        log.info("Completed tournament: {}", tournament.getName());
    }

    @Override
    public void cancelTournament(Long id, String reason) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        tournament.cancel(reason);
        tournamentRepository.save(tournament);

        // Notify all participants
        messagingTemplate.convertAndSend(
                "/topic/tournament/" + id + "/status",
                Map.of("status", "CANCELLED", "message", "Tournament has been cancelled", "reason", reason));

        log.info("Cancelled tournament: {} - Reason: {}", tournament.getName(), reason);
    }

    @Override
    public void generateBracket(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        List<TournamentParticipant> participants = participantRepository
                .findByTournamentIdAndStatus(tournamentId, ParticipantStatus.CONFIRMED);

        if (participants.isEmpty()) {
            throw new BadRequestException("No confirmed participants to generate bracket");
        }

        // Shuffle participants if no seeding
        List<TournamentParticipant> shuffledParticipants = new ArrayList<>(participants);
        Collections.shuffle(shuffledParticipants);

        // Calculate number of rounds
        int participantCount = participants.size();
        int roundCount = (int) Math.ceil(Math.log(participantCount) / Math.log(2));

        // Create rounds
        List<TournamentRound> rounds = new ArrayList<>();
        for (int i = 1; i <= roundCount; i++) {
            String roundName = getRoundName(i, roundCount);

            TournamentRound round = TournamentRound.builder()
                    .tournament(tournament)
                    .roundNumber(i)
                    .name(roundName)
                    .totalMatches((int) Math.pow(2, roundCount - i))
                    .build();

            rounds.add(roundRepository.save(round));
        }

        // Generate first round matches
        TournamentRound firstRound = rounds.get(0);
        int matchNumber = 1;

        for (int i = 0; i < shuffledParticipants.size(); i += 2) {
            TournamentParticipant p1 = shuffledParticipants.get(i);
            TournamentParticipant p2 = i + 1 < shuffledParticipants.size() ? shuffledParticipants.get(i + 1) : null;

            TournamentMatch match = TournamentMatch.builder()
                    .tournament(tournament)
                    .round(firstRound)
                    .matchNumber(matchNumber++)
                    .participant1(p1)
                    .participant2(p2)
                    .status(p2 != null ? MatchStatus.SCHEDULED : MatchStatus.WALKOVER)
                    .court(tournament.getCourt())
                    .build();

            // If no opponent, declare walkover
            if (p2 == null) {
                match.declareWalkover(p1, "No opponent");
            }

            matchRepository.save(match);
        }

        log.info("Generated bracket for tournament: {}", tournament.getName());
    }

    private String getRoundName(int roundNumber, int totalRounds) {
        int remainingRounds = totalRounds - roundNumber;

        return switch (remainingRounds) {
            case 0 -> "Final";
            case 1 -> "Semi-finals";
            case 2 -> "Quarter-finals";
            case 3 -> "Round of 16";
            case 4 -> "Round of 32";
            default -> "Round " + roundNumber;
        };
    }

    // ===== MATCHES =====

    @Override
    public TournamentMatchDTO createMatch(CreateMatchRequest request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        TournamentParticipant p1 = participantRepository.findById(request.getParticipant1Id())
                .orElseThrow(() -> new ResourceNotFoundException("Participant 1 not found"));

        TournamentParticipant p2 = null;
        if (request.getParticipant2Id() != null) {
            p2 = participantRepository.findById(request.getParticipant2Id())
                    .orElseThrow(() -> new ResourceNotFoundException("Participant 2 not found"));
        }

        TournamentRound round = null;
        if (request.getRoundId() != null) {
            round = roundRepository.findById(request.getRoundId())
                    .orElseThrow(() -> new ResourceNotFoundException("Round not found"));
        }

        Court court = null;
        if (request.getCourtId() != null) {
            court = courtRepository.findById(request.getCourtId())
                    .orElseThrow(() -> new ResourceNotFoundException("Court not found"));
        }

        TournamentMatch match = TournamentMatch.builder()
                .tournament(tournament)
                .round(round)
                .matchNumber(request.getMatchNumber())
                .participant1(p1)
                .participant2(p2)
                .status(MatchStatus.SCHEDULED)
                .scheduledTime(request.getScheduledTime())
                .court(court)
                .refereeName(request.getRefereeName())
                .build();

        match = matchRepository.save(match);

        // Broadcast new match
        messagingTemplate.convertAndSend(
                "/topic/tournament/" + tournament.getId() + "/matches",
                tournamentMapper.toMatchDTO(match));

        log.info("Created match {} in tournament {}", match.getId(), tournament.getId());
        return tournamentMapper.toMatchDTO(match);
    }

    @Override
    public TournamentMatchDTO updateMatchScore(Long matchId, UpdateMatchScoreRequest request) {
        TournamentMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        match.setParticipant1Score(request.getParticipant1Score());
        match.setParticipant2Score(request.getParticipant2Score());
        match.setParticipant1Set1(request.getParticipant1Set1());
        match.setParticipant1Set2(request.getParticipant1Set2());
        match.setParticipant1Set3(request.getParticipant1Set3());
        match.setParticipant2Set1(request.getParticipant2Set1());
        match.setParticipant2Set2(request.getParticipant2Set2());
        match.setParticipant2Set3(request.getParticipant2Set3());

        match = matchRepository.save(match);

        // Broadcast score update
        messagingTemplate.convertAndSend(
                "/topic/tournament/" + match.getTournament().getId() + "/match/" + matchId + "/score",
                tournamentMapper.toMatchDTO(match));

        log.info("Updated score for match {}", matchId);
        return tournamentMapper.toMatchDTO(match);
    }

    @Override
    public void startMatch(Long matchId) {
        TournamentMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        if (match.getStatus() != MatchStatus.SCHEDULED) {
            throw new BadRequestException("Match is not scheduled");
        }

        match.start();
        matchRepository.save(match);

        // Broadcast match started
        messagingTemplate.convertAndSend(
                "/topic/tournament/" + match.getTournament().getId() + "/match/" + matchId + "/status",
                Map.of("status", "STARTED", "matchId", matchId));

        log.info("Started match {}", matchId);
    }

    @Override
    public void completeMatch(Long matchId, Long winnerId) {
        TournamentMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        TournamentParticipant winner = participantRepository.findById(winnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Winner not found"));

        if (match.getStatus() != MatchStatus.IN_PROGRESS) {
            throw new BadRequestException("Match is not in progress");
        }

        match.complete(winner);
        matchRepository.save(match);

        // Update round completion
        if (match.getRound() != null) {
            TournamentRound round = match.getRound();
            round.incrementCompletedMatches();
            roundRepository.save(round);
        }

        // Broadcast match completed
        messagingTemplate.convertAndSend(
                "/topic/tournament/" + match.getTournament().getId() + "/match/" + matchId + "/status",
                Map.of(
                        "status", "COMPLETED",
                        "matchId", matchId,
                        "winnerId", winnerId,
                        "winnerName", winner.getUser().getName()));

        log.info("Completed match {} - Winner: {}", matchId, winner.getUser().getName());
    }

    @Override
    public void cancelMatch(Long matchId, String reason) {
        TournamentMatch match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        match.cancel(reason);
        matchRepository.save(match);

        log.info("Cancelled match {} - Reason: {}", matchId, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentMatchDTO> getTournamentMatches(Long tournamentId) {
        List<TournamentMatch> matches = matchRepository.findByTournamentId(tournamentId);
        return matches.stream()
                .map(tournamentMapper::toMatchDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentMatchDTO> getLiveMatches(Long tournamentId) {
        List<TournamentMatch> matches = matchRepository.findLiveMatches(tournamentId);
        return matches.stream()
                .map(tournamentMapper::toMatchDTO)
                .collect(Collectors.toList());
    }

    // ===== PARTICIPANTS =====

    @Override
    @Transactional(readOnly = true)
    public List<TournamentParticipantDTO> getTournamentParticipants(Long tournamentId) {
        List<TournamentParticipant> participants = participantRepository
                .findByTournamentId(tournamentId);
        return participants.stream()
                .map(tournamentMapper::toParticipantDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentParticipantDTO> getLeaderboard(Long tournamentId) {
        List<TournamentParticipant> participants = participantRepository
                .getLeaderboard(tournamentId);
        return participants.stream()
                .map(tournamentMapper::toParticipantDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentParticipantDTO getParticipantById(Long id) {
        TournamentParticipant participant = participantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));
        return tournamentMapper.toParticipantDTO(participant);
    }

    @Override
    public void updateParticipantSeed(Long participantId, Integer seedNumber) {
        TournamentParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.setSeedNumber(seedNumber);
        participantRepository.save(participant);

        log.info("Updated seed number for participant {} to {}", participantId, seedNumber);
    }

    @Override
    public void disqualifyParticipant(Long participantId, String reason) {
        TournamentParticipant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found"));

        participant.disqualify(reason);
        participantRepository.save(participant);

        log.info("Disqualified participant {} - Reason: {}", participantId, reason);
    }

    // ===== ROUNDS =====

    @Override
    public TournamentRoundDTO createRound(CreateRoundRequest request) {
        Tournament tournament = tournamentRepository.findById(request.getTournamentId())
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        TournamentRound round = TournamentRound.builder()
                .tournament(tournament)
                .roundNumber(request.getRoundNumber())
                .name(request.getName())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalMatches(request.getTotalMatches())
                .build();

        round = roundRepository.save(round);

        log.info("Created round {} for tournament {}", round.getName(), tournament.getName());
        return tournamentMapper.toRoundDTO(round);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentRoundDTO> getTournamentRounds(Long tournamentId) {
        List<TournamentRound> rounds = roundRepository
                .findByTournamentIdOrderByRoundNumber(tournamentId);
        return rounds.stream()
                .map(tournamentMapper::toRoundDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TournamentRoundDTO getCurrentRound(Long tournamentId) {
        TournamentRound round = roundRepository.findCurrentRound(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("No current round found"));
        return tournamentMapper.toRoundDTO(round);
    }

    // ===== STATISTICS =====

    @Override
    @Transactional(readOnly = true)
    public TournamentStatsDTO getTournamentStatistics(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament not found"));

        Long totalParticipants = participantRepository.countActiveParticipants(tournamentId);
        Long totalMatches = matchRepository.countCompletedMatches(tournamentId);

        return TournamentStatsDTO.builder()
                .tournamentId(tournamentId)
                .tournamentName(tournament.getName())
                .status(tournament.getStatus())
                .totalParticipants(totalParticipants.intValue())
                .totalMatches(totalMatches.intValue())
                .prizePool(tournament.getPrizePool())
                .startDate(tournament.getStartDate())
                .endDate(tournament.getEndDate())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TournamentDTO> getUserTournaments(Long userId) {
        List<Tournament> tournaments = tournamentRepository.findByParticipantUserId(userId);
        return tournaments.stream()
                .map(tournamentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TournamentDTO> searchTournaments(String searchTerm, String status, Pageable pageable) {
        TournamentStatus tournamentStatus = status != null ? TournamentStatus.valueOf(status) : null;
        Page<Tournament> tournaments = tournamentRepository.searchTournaments(
                searchTerm, tournamentStatus, pageable);
        return tournaments.map(tournamentMapper::toDTO);
    }
}
