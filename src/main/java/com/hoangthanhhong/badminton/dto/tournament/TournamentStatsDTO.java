package com.hoangthanhhong.badminton.dto.tournament;

import com.hoangthanhhong.badminton.enums.TournamentStatus;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentStatsDTO {
    private Long tournamentId;
    private String tournamentName;
    private TournamentStatus status;
    private Integer totalParticipants;
    private Integer totalMatches;
    private Double prizePool;
    private LocalDate startDate;
    private LocalDate endDate;
}
