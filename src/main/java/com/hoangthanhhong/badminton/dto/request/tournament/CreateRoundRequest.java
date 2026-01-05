package com.hoangthanhhong.badminton.dto.request.tournament;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRoundRequest {
    private Long tournamentId;
    private Integer roundNumber;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalMatches;
}
