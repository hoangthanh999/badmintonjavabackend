package com.hoangthanhhong.badminton.dto.review;

import lombok.*;
import java.util.Map;

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
    private Double fiveStarsPercentage;
    private Double fourStarsPercentage;
    private Double threeStarsPercentage;
    private Double twoStarsPercentage;
    private Double oneStarPercentage;
    private Map<Integer, Long> ratingDistribution;
}
