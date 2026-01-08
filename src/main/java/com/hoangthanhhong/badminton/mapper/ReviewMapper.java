package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.review.ReviewDTO;
import com.hoangthanhhong.badminton.dto.review.ReviewStatisticsDTO;
import com.hoangthanhhong.badminton.entity.Review;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ReviewMapper {

    public ReviewDTO toDTO(Review review) {
        if (review == null)
            return null;

        // Calculate average detailed rating
        Double averageDetailedRating = calculateAverageDetailedRating(
                review.getQualityRating(),
                review.getServiceRating(),
                review.getCleanlinessRating(),
                review.getValueRating(),
                review.getLocationRating());

        // Calculate helpfulness score
        Integer helpfulnessScore = (review.getHelpfulCount() != null ? review.getHelpfulCount() : 0) -
                (review.getNotHelpfulCount() != null ? review.getNotHelpfulCount() : 0);

        return ReviewDTO.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .userAvatar(review.getUser().getAvatar())
                .reviewType(review.getReviewType())
                .courtId(review.getCourt() != null ? review.getCourt().getId() : null)
                .courtName(review.getCourt() != null ? review.getCourt().getName() : null)
                .productId(review.getProduct() != null ? review.getProduct().getId() : null)
                .productName(review.getProduct() != null ? review.getProduct().getName() : null)
                .bookingId(review.getBooking() != null ? review.getBooking().getId() : null)
                .orderId(review.getOrder() != null ? review.getOrder().getId() : null)
                .rating(review.getRating())
                .qualityRating(review.getQualityRating())
                .serviceRating(review.getServiceRating())
                .cleanlinessRating(review.getCleanlinessRating())
                .valueRating(review.getValueRating())
                .locationRating(review.getLocationRating())
                .averageDetailedRating(averageDetailedRating)
                .title(review.getTitle())
                .comment(review.getComment())
                .status(review.getStatus())
                .images(parseJsonArray(review.getImages()))
                .videos(parseJsonArray(review.getVideos()))
                .isVerifiedPurchase(review.getIsVerifiedPurchase())
                .verifiedAt(review.getVerifiedAt())
                .isFeatured(review.getIsFeatured())
                .isReported(review.getIsReported())
                .reportCount(review.getReportCount())
                .approvedAt(review.getApprovedAt())
                .approvedBy(review.getApprovedBy())
                .rejectedAt(review.getRejectedAt())
                .rejectedBy(review.getRejectedBy())
                .rejectionReason(review.getRejectionReason())
                .helpfulCount(review.getHelpfulCount())
                .notHelpfulCount(review.getNotHelpfulCount())
                .helpfulnessScore(helpfulnessScore)
                .adminResponse(review.getAdminResponse())
                .respondedAt(review.getRespondedAt())
                .respondedBy(review.getRespondedBy())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    public ReviewStatisticsDTO toStatisticsDTO(Object[] stats) {
        if (stats == null || stats.length < 7)
            return null;

        Long totalReviews = ((Number) stats[0]).longValue();
        Double averageRating = stats[1] != null ? ((Number) stats[1]).doubleValue() : 0.0;
        Long fiveStars = ((Number) stats[2]).longValue();
        Long fourStars = ((Number) stats[3]).longValue();
        Long threeStars = ((Number) stats[4]).longValue();
        Long twoStars = ((Number) stats[5]).longValue();
        Long oneStar = ((Number) stats[6]).longValue();

        // Calculate percentages
        Double fiveStarsPercentage = totalReviews > 0 ? (fiveStars * 100.0 / totalReviews) : 0.0;
        Double fourStarsPercentage = totalReviews > 0 ? (fourStars * 100.0 / totalReviews) : 0.0;
        Double threeStarsPercentage = totalReviews > 0 ? (threeStars * 100.0 / totalReviews) : 0.0;
        Double twoStarsPercentage = totalReviews > 0 ? (twoStars * 100.0 / totalReviews) : 0.0;
        Double oneStarPercentage = totalReviews > 0 ? (oneStar * 100.0 / totalReviews) : 0.0;

        return ReviewStatisticsDTO.builder()
                .totalReviews(totalReviews)
                .averageRating(averageRating)
                .fiveStars(fiveStars)
                .fourStars(fourStars)
                .threeStars(threeStars)
                .twoStars(twoStars)
                .oneStar(oneStar)
                .fiveStarsPercentage(fiveStarsPercentage)
                .fourStarsPercentage(fourStarsPercentage)
                .threeStarsPercentage(threeStarsPercentage)
                .twoStarsPercentage(twoStarsPercentage)
                .oneStarPercentage(oneStarPercentage)
                .ratingDistribution(Map.of(
                        5, fiveStars,
                        4, fourStars,
                        3, threeStars,
                        2, twoStars,
                        1, oneStar))
                .build();
    }

    private Double calculateAverageDetailedRating(Integer... ratings) {
        int count = 0;
        int sum = 0;

        for (Integer rating : ratings) {
            if (rating != null) {
                sum += rating;
                count++;
            }
        }

        return count > 0 ? (sum * 1.0 / count) : null;
    }

    private List<String> parseJsonArray(List<String> list) {
        // Review entity đã lưu List<String>, không cần parse
        return list != null ? list : Collections.emptyList();
    }

}
