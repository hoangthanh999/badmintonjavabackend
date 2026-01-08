
package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.review.ReviewDTO;
import com.hoangthanhhong.badminton.dto.review.ReviewStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.review.CreateReviewRequest;
import com.hoangthanhhong.badminton.dto.request.review.UpdateReviewRequest;
import com.hoangthanhhong.badminton.entity.*;
import com.hoangthanhhong.badminton.enums.ReviewStatus;
import com.hoangthanhhong.badminton.enums.ReviewType;
import com.hoangthanhhong.badminton.exception.BadRequestException;
import com.hoangthanhhong.badminton.exception.ForbiddenException;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.ReviewMapper;
import com.hoangthanhhong.badminton.repository.*;
import com.hoangthanhhong.badminton.service.LoyaltyService;
import com.hoangthanhhong.badminton.service.NotificationService;
import com.hoangthanhhong.badminton.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final CourtRepository courtRepository;
    private final ProductRepository productRepository;
    private final BookingRepository bookingRepository;
    private final OrderRepository orderRepository;
    private final ReviewHelpfulnessRepository helpfulnessRepository;

    private final ReviewReportRepository reportRepository;
    private final ReviewMapper reviewMapper;
    private final NotificationService notificationService;
    private final LoyaltyService loyaltyService;

    // ===== CRUD =====

    @Override
    public ReviewDTO createReview(CreateReviewRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate that at least one entity is specified
        if (request.getCourtId() == null && request.getProductId() == null) {
            throw new BadRequestException("Must specify either court or product to review");
        }

        // Check if user already reviewed this entity
        if (request.getCourtId() != null) {
            boolean exists = reviewRepository.existsActiveCourtReview(
                    userId, request.getCourtId());
            if (exists) {
                throw new BadRequestException("You have already reviewed this court");
            }
        }

        if (request.getProductId() != null) {
            boolean exists = reviewRepository.existsActiveProductReview(
                    userId, request.getProductId());
            if (exists) {
                throw new BadRequestException("You have already reviewed this product");
            }
        }

        Review review = Review.builder()
                .user(user)
                .reviewType(request.getReviewType())
                .rating(request.getRating())
                .qualityRating(request.getQualityRating())
                .serviceRating(request.getServiceRating())
                .cleanlinessRating(request.getCleanlinessRating())
                .valueRating(request.getValueRating())
                .locationRating(request.getLocationRating())
                .title(request.getTitle())
                .comment(request.getComment())
                .status(ReviewStatus.PENDING)
                .isVerifiedPurchase(false)
                .isFeatured(false)
                .isReported(false)
                .reportCount(0)
                .helpfulCount(0)
                .notHelpfulCount(0)
                .build();

        // Set related entities
        if (request.getCourtId() != null) {
            Court court = courtRepository.findById(request.getCourtId())
                    .orElseThrow(() -> new ResourceNotFoundException("Court not found"));
            review.setCourt(court);
        }

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            review.setProduct(product);
        }

        if (request.getBookingId() != null) {
            Booking booking = bookingRepository.findById(request.getBookingId())
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
            review.setBooking(booking);
            review.setIsVerifiedPurchase(true);
        }

        if (request.getOrderId() != null) {
            Order order = orderRepository.findById(request.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            review.setOrder(order);
            review.setIsVerifiedPurchase(true);
        }

        // Handle images

        if (request.getImages() != null && !request.getImages().isEmpty()) {
            review.setImages(request.getImages());
        }

        if (request.getVideos() != null && !request.getVideos().isEmpty()) {
            review.setVideos(request.getVideos());
        }

        review = reviewRepository.save(review);

        // Reward user with loyalty points
        loyaltyService.rewardReviewSubmission(review.getId(), userId);

        log.info("User {} created review {}", userId, review.getId());

        return reviewMapper.toDTO(review);
    }

    @Override
    public ReviewDTO updateReview(Long id, UpdateReviewRequest request, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Check ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only update your own reviews");
        }

        // Check if review is approved (can't edit approved reviews)
        if (review.getStatus() == ReviewStatus.APPROVED) {
            throw new BadRequestException("Cannot edit approved reviews");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        review.setUpdatedAt(LocalDateTime.now());
        review = reviewRepository.save(review);

        log.info("User {} updated review {}", userId, id);

        return reviewMapper.toDTO(review);
    }

    @Override
    public void deleteReview(Long id, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        // Check ownership
        if (!review.getUser().getId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own reviews");
        }

        review.softDelete();
        reviewRepository.save(review);

        log.info("User {} deleted review {}", userId, id);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        return reviewMapper.toDTO(review);
    }

    // ===== GET REVIEWS =====

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getCourtReviews(Long courtId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByCourtIdAndStatusAndIsDeletedFalse(
                courtId, ReviewStatus.APPROVED, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getProductReviews(Long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductIdAndStatusAndIsDeletedFalse(
                productId, ReviewStatus.APPROVED, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getUserReviews(Long userId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    // ===== STATISTICS =====

    @Override
    @Transactional(readOnly = true)
    public ReviewStatisticsDTO getCourtReviewStatistics(Long courtId) {
        Object[] stats = reviewRepository.getReviewStatistics(courtId, null);
        return reviewMapper.toStatisticsDTO(stats);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewStatisticsDTO getProductReviewStatistics(Long productId) {
        Object[] stats = reviewRepository.getReviewStatistics(null, productId);
        return reviewMapper.toStatisticsDTO(stats);
    }

    // ===== MODERATION =====

    @Override
    public void approveReview(Long id, Long approvedBy) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.approve(approvedBy);
        reviewRepository.save(review);

        // Notify user
        notificationService.sendNotification(
                review.getUser().getId(),
                com.hoangthanhhong.badminton.enums.NotificationType.REVIEW_APPROVED,
                "Review Approved",
                "Your review has been approved and is now visible to others",
                java.util.Map.of("reviewId", id));

        log.info("Review {} approved by {}", id, approvedBy);
    }

    @Override
    public void rejectReview(Long id, Long rejectedBy, String reason) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.reject(rejectedBy, reason);
        reviewRepository.save(review);

        // Notify user
        notificationService.sendNotification(
                review.getUser().getId(),
                com.hoangthanhhong.badminton.enums.NotificationType.REVIEW_REJECTED,
                "Review Rejected",
                String.format("Your review has been rejected: %s", reason),
                java.util.Map.of("reviewId", id, "reason", reason));

        log.info("Review {} rejected by {}: {}", id, rejectedBy, reason);
    }

    @Override
    public void markAsSpam(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.markAsSpam();
        reviewRepository.save(review);

        log.info("Review {} marked as spam", id);
    }

    @Override
    public void featureReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.feature();
        reviewRepository.save(review);

        log.info("Review {} featured", id);
    }

    @Override
    public void unfeatureReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.unfeature();
        reviewRepository.save(review);

        log.info("Review {} unfeatured", id);
    }

    // ===== HELPFULNESS =====

    @Override
    public void markAsHelpful(Long reviewId, Long userId, Boolean isHelpful) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if already voted
        ReviewHelpful existing = helpfulnessRepository.findByReviewIdAndUserId(reviewId, userId)
                .orElse(null);

        if (existing != null) {
            // Update vote
            if (!existing.getIsHelpful().equals(isHelpful)) {
                if (existing.getIsHelpful()) {
                    review.decrementHelpful();
                    review.incrementNotHelpful();
                } else {
                    review.decrementNotHelpful();
                    review.incrementHelpful();
                }
                existing.setIsHelpful(isHelpful);
                helpfulnessRepository.save(existing);
            }
        } else {
            // Create new vote
            ReviewHelpful helpful = ReviewHelpful.builder()
                    .review(review)
                    .user(user)
                    .isHelpful(isHelpful)
                    .build();

            helpfulnessRepository.save(helpful);

            if (isHelpful) {
                review.incrementHelpful();
            } else {
                review.incrementNotHelpful();
            }
        }

        reviewRepository.save(review);
    }

    @Override
    public void removeHelpfulVote(Long reviewId, Long userId) {
        ReviewHelpful helpful = helpfulnessRepository
                .findByReviewIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Vote not found"));

        Review review = helpful.getReview();

        if (helpful.getIsHelpful()) {
            review.decrementHelpful();
        } else {
            review.decrementNotHelpful();
        }

        helpfulnessRepository.delete(helpful);
        reviewRepository.save(review);
    }

    // ===== REPORTING =====

    @Override
    public void reportReview(Long reviewId, Long reporterId, String reason, String description) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ReviewReport report = ReviewReport.builder()
                .review(review)
                .reporter(reporter)
                .reason(reason)
                .description(description)
                .status("PENDING")
                .build();

        reportRepository.save(report);

        // Update review
        review.report();
        reviewRepository.save(review);

        log.info("Review {} reported by user {}: {}", reviewId, reporterId, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReportedReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findReportedReviews(pageable);
        return reviews.map(reviewMapper::toDTO);
    }

    // ===== ADMIN RESPONSE =====

    @Override
    public void addAdminResponse(Long reviewId, String response, Long respondedBy) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        review.respond(response, respondedBy);
        reviewRepository.save(review);

        // Notify user
        notificationService.sendNotification(
                review.getUser().getId(),
                com.hoangthanhhong.badminton.enums.NotificationType.REVIEW_RESPONDED,
                "Admin Responded to Your Review",
                "An admin has responded to your review",
                java.util.Map.of("reviewId", reviewId));

        log.info("Admin {} responded to review {}", respondedBy, reviewId);
    }

    // ===== SEARCH =====

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> searchReviews(String searchTerm, Pageable pageable) {
        Page<Review> reviews = reviewRepository.searchReviews(searchTerm, pageable);
        return reviews.map(reviewMapper::toDTO);
    }
}
