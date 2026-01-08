package com.hoangthanhhong.badminton.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtAvailabilityRequest {

    private LocalDate date;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String location;

    private Double minPrice;

    private Double maxPrice;

    private Boolean hasAirConditioning;

    private Double minRating;
}
