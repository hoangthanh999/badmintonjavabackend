package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.review.ReviewDTO;
import com.hoangthanhhong.badminton.dto.review.ReviewStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.review.CreateReviewRequest;
import com.hoangthanhhong.badminton.dto.request.review.UpdateReviewRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    // CRUD
    ReviewDTO createReview(CreateReviewRequest request, Long userId);

    ReviewDTO updateReview(Long id, UpdateReviewRequest request, Long userId);

    void deleteReview(Long id, Long userId);

    ReviewDTO getReviewById(Long id);

    // Get reviews
    Page<ReviewDTO> getCourtReviews(Long courtId, Pageable pageable);

    Page<ReviewDTO> getProductReviews(Long productId, Pageable pageable);

    Page<ReviewDTO> getUserReviews(Long userId, Pageable pageable);

    // Statistics
    ReviewStatisticsDTO getCourtReviewStatistics(Long courtId);

    ReviewStatisticsDTO getProductReviewStatistics(Long productId);

    // Moderation
    void approveReview(Long id, Long approvedBy);

    void rejectReview(Long id, Long rejectedBy, String reason);

    void markAsSpam(Long id);

    void featureReview(Long id);

    void unfeatureReview(Long id);

    // Helpfulness
    void markAsHelpful(Long reviewId, Long userId, Boolean isHelpful);

    void removeHelpfulVote(Long reviewId, Long userId);

    // Reporting
    void reportReview(Long reviewId, Long reporterId, String reason, String description);

    Page<ReviewDTO> getReportedReviews(Pageable pageable);

    // Admin response
    void addAdminResponse(Long reviewId, String response, Long respondedBy);

    // Search
    Page<ReviewDTO> searchReviews(String searchTerm, Pageable pageable);
}
