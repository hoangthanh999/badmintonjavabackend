package com.hoangthanhhong.badminton.dto.tournament;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentRoundDTO {
    private Long id;
    private Long tournamentId;
    private Integer roundNumber;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalMatches;
    private Integer completedMatches;
    private Boolean isCompleted;
}
