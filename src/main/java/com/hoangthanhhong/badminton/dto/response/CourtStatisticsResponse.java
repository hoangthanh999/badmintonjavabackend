package com.hoangthanhhong.badminton.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtStatisticsResponse {

    private Long totalCourts;

    private Long availableCourts;

    private Long occupiedCourts;

    private Long maintenanceCourts;

    private Long closedCourts;

    private Double averageRating;

    private Double averagePrice;

    private Double minPrice;

    private Double maxPrice;

    private Long totalBookings;

    private Long totalReviews;
}