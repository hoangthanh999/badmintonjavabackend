package com.hoangthanhhong.badminton.dto.tournament;

import com.hoangthanhhong.badminton.enums.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TournamentDTO {
    private Long id;
    private String name;
    private String description;
    private Long courtId;
    private String courtName;
    private TournamentType tournamentType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    private TournamentStatus status;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer minParticipants;
    private Double entryFee;
    private Double prizePool;
    private String imageUrl;
    private String location;
    private Boolean isPublic;
    private Boolean isFeatured;
    private LocalDateTime createdAt;
}
