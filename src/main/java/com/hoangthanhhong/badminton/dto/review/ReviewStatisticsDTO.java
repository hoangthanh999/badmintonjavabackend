package com.hoangthanhhong.badminton.dto.review;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewStatisticsDTO {
    private Long totalReviews;
    private Double averageRating;
    private Long fiveStars;
    private Long fourStars;
    private Long threeStars;
    private Long twoStars;
    private Long oneStar;
}
