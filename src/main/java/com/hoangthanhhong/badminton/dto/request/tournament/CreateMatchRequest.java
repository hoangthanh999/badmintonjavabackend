package com.hoangthanhhong.badminton.dto.request.tournament;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMatchRequest {
    private Long tournamentId;
    private Long roundId;
    private Integer matchNumber;
    private Long participant1Id;
    private Long participant2Id;
    private Long courtId;
    private LocalDateTime scheduledTime;
    private String refereeName;
}
