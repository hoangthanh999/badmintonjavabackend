package com.hoangthanhhong.badminton.dto.request.tournament;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTournamentRequest {
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxParticipants;
    private Double entryFee;
    private Double prizePool;
    private String imageUrl;
    private String rules;
    private String location;
}
