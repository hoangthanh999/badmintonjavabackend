package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.ReviewStatus;
import com.hoangthanhhong.badminton.enums.ReviewType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_court_id", columnList = "court_id"),
        @Index(name = "idx_product_id", columnList = "product_id"),
        @Index(name = "idx_booking_id", columnList = "booking_id"),
        @Index(name = "idx_order_id", columnList = "order_id"),
        @Index(name = "idx_rating", columnList = "rating"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_type", nullable = false, length = 20)
    private ReviewType reviewType; // COURT, PRODUCT, SERVICE, ORDER

    // === REVIEWABLE ENTITIES ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // === REVIEW CONTENT ===

    @Column(nullable = false)
    private Integer rating; // 1-5 stars

    @Column(length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.PENDING;

    // === DETAILED RATINGS ===

    @Column(name = "quality_rating")
    private Integer qualityRating; // For products

    @Column(name = "service_rating")
    private Integer serviceRating;

    @Column(name = "cleanliness_rating")
    private Integer cleanlinessRating; // For courts

    @Column(name = "value_rating")
    private Integer valueRating; // Value for money

    @Column(name = "location_rating")
    private Integer locationRating; // For courts

    // === MEDIA ===

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url", length = 500)
    @Builder.Default
    private List<String> images = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "review_videos", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "video_url", length = 500)
    @Builder.Default
    private List<String> videos = new ArrayList<>();

    // === VERIFICATION ===

    @Column(name = "is_verified_purchase")
    @Builder.Default
    private Boolean isVerifiedPurchase = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // === MODERATION ===

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_reported")
    @Builder.Default
    private Boolean isReported = false;

    @Column(name = "report_count")
    @Builder.Default
    private Integer reportCount = 0;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "rejected_by")
    private Long rejectedBy;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // === HELPFULNESS ===

    @Column(name = "helpful_count")
    @Builder.Default
    private Integer helpfulCount = 0;

    @Column(name = "not_helpful_count")
    @Builder.Default
    private Integer notHelpfulCount = 0;

    // === RESPONSE ===

    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "responded_by")
    private Long respondedBy;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewHelpful> helpfulVotes = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewReport> reports = new ArrayList<>();

    // === HELPER METHODS ===

    public void approve(Long approvedBy) {
        this.status = ReviewStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approvedBy;
    }

    public void reject(Long rejectedBy, String reason) {
        this.status = ReviewStatus.REJECTED;
        this.rejectedAt = LocalDateTime.now();
        this.rejectedBy = rejectedBy;
        this.rejectionReason = reason;
    }

    public void markAsSpam() {
        this.status = ReviewStatus.SPAM;
    }

    public void verify() {
        this.isVerifiedPurchase = true;
        this.verifiedAt = LocalDateTime.now();
    }

    public void feature() {
        this.isFeatured = true;
    }

    public void unfeature() {
        this.isFeatured = false;
    }

    public void incrementHelpful() {
        this.helpfulCount++;
    }

    public void decrementHelpful() {
        if (this.helpfulCount > 0) {
            this.helpfulCount--;
        }
    }

    public void incrementNotHelpful() {
        this.notHelpfulCount++;
    }

    public void decrementNotHelpful() {
        if (this.notHelpfulCount > 0) {
            this.notHelpfulCount--;
        }
    }

    public void report() {
        this.isReported = true;
        this.reportCount++;
    }

    public void addAdminResponse(String response, Long respondedBy) {
        this.adminResponse = response;
        this.respondedAt = LocalDateTime.now();
        this.respondedBy = respondedBy;
    }

    public Double getAverageDetailedRating() {
        int count = 0;
        int total = 0;

        if (qualityRating != null) {
            total += qualityRating;
            count++;
        }
        if (serviceRating != null) {
            total += serviceRating;
            count++;
        }
        if (cleanlinessRating != null) {
            total += cleanlinessRating;
            count++;
        }
        if (valueRating != null) {
            total += valueRating;
            count++;
        }
        if (locationRating != null) {
            total += locationRating;
            count++;
        }

        return count > 0 ? (double) total / count : 0.0;
    }

    public Integer getHelpfulnessScore() {
        return helpfulCount - notHelpfulCount;
    }

    public boolean isApproved() {
        return status == ReviewStatus.APPROVED;
    }

    public boolean isPending() {
        return status == ReviewStatus.PENDING;
    }

    public void respond(String response, Long respondedBy) {
        this.adminResponse = response;
        this.respondedAt = LocalDateTime.now();
        this.respondedBy = respondedBy;
    }
}
