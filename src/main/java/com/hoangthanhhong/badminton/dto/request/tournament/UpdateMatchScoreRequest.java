package com.hoangthanhhong.badminton.dto.request.tournament;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMatchScoreRequest {
    private Integer participant1Score;
    private Integer participant2Score;
    private Integer participant1Set1;
    private Integer participant1Set2;
    private Integer participant1Set3;
    private Integer participant2Set1;
    private Integer participant2Set2;
    private Integer participant2Set3;
}
