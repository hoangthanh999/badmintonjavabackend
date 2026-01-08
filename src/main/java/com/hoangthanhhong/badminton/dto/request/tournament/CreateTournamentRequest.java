package com.hoangthanhhong.badminton.dto.request.tournament;

import com.hoangthanhhong.badminton.enums.TournamentType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTournamentRequest {
    @NotBlank
    private String name;
    private String description;
    private Long courtId;
    @NotNull
    private TournamentType tournamentType;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private LocalDate registrationStartDate;
    private LocalDate registrationEndDate;
    @NotNull
    private Integer maxParticipants;
    private Integer minParticipants;
    private Double entryFee;
    private Double prizePool;
    private Double firstPrize;
    private Double secondPrize;
    private Double thirdPrize;
    private String imageUrl;
    private String bannerUrl;
    private String rules;
    private String format;
    private String ageRestriction;
    private String skillLevel;
    private String contactEmail;
    private String contactPhone;
    private Boolean isPublic;
    private Boolean isFeatured;
    private Long organizerId;
    private String organizerName;
    private String sponsor;
    private String location;
}
