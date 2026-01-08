// File: ReviewDTO.java (CẬP NHẬT - Phần User)
package com.hoangthanhhong.badminton.dto.review;

import com.hoangthanhhong.badminton.enums.ReviewStatus;
import com.hoangthanhhong.badminton.enums.ReviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {
    private Long id;

    // User - ✅ THÊM userAvatar
    private Long userId;
    private String userName;
    private String userAvatar; // ✅ THÊM FIELD NÀY

    // Type
    private ReviewType reviewType;

    // Related entities
    private Long courtId;
    private String courtName;
    private Long productId;
    private String productName;
    private Long bookingId;
    private Long orderId;

    // Rating
    private Integer rating;
    private Integer qualityRating;
    private Integer serviceRating;
    private Integer cleanlinessRating;
    private Integer valueRating;
    private Integer locationRating;
    private Double averageDetailedRating;

    // Content
    private String title;
    private String comment;

    // Status
    private ReviewStatus status;

    // Media
    private List<String> images;
    private List<String> videos;

    // Verification
    private Boolean isVerifiedPurchase;
    private LocalDateTime verifiedAt;

    // Moderation
    private Boolean isFeatured;
    private Boolean isReported;
    private Integer reportCount;

    // Approval
    private LocalDateTime approvedAt;
    private Long approvedBy;
    private LocalDateTime rejectedAt;
    private Long rejectedBy;
    private String rejectionReason;

    // Helpfulness
    private Integer helpfulCount;
    private Integer notHelpfulCount;
    private Integer helpfulnessScore;

    // Response
    private String adminResponse;
    private LocalDateTime respondedAt;
    private Long respondedBy;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
