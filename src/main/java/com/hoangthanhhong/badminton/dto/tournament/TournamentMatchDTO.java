package com.hoangthanhhong.badminton.dto.tournament;

import com.hoangthanhhong.badminton.enums.MatchStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentMatchDTO {
    private Long id;
    private Long tournamentId;
    private Integer matchNumber;
    private Long participant1Id;
    private String participant1Name;
    private Long participant2Id;
    private String participant2Name;
    private Integer participant1Score;
    private Integer participant2Score;
    private MatchStatus status;
    private LocalDateTime scheduledTime;
    private Long winnerId;
    private String winnerName;
}
