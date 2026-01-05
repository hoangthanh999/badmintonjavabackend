package com.hoangthanhhong.badminton.dto.request.tournament;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterTournamentRequest {
    private Long partnerId; // For doubles
    private String teamName;
    private Integer jerseyNumber;
    private String notes;
}