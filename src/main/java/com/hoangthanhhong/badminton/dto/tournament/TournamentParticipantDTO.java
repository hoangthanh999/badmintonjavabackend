
package com.hoangthanhhong.badminton.dto.tournament;

import com.hoangthanhhong.badminton.enums.ParticipantStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentParticipantDTO {
    private Long id;
    private Long tournamentId;
    private Long userId;
    private String userName;
    private String teamName;
    private ParticipantStatus status;
    private Integer seedNumber;
    private Integer matchesPlayed;
    private Integer matchesWon;
    private Integer matchesLost;
    private Double winRate;
    private Integer finalRank;
    private LocalDateTime registeredAt;
}
