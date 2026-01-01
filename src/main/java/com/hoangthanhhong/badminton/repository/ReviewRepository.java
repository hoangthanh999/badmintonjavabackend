package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Review;
import com.hoangthanhhong.badminton.enums.ReviewStatus;
import com.hoangthanhhong.badminton.enums.ReviewType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // === BASIC QUERIES ===

    List<Review> findByUserId(Long userId);

    List<Review> findByCourtId(Long courtId);

    List<Review> findByProductId(Long productId);

    Optional<Review> findByBookingId(Long bookingId);

    Optional<Review> findByOrderId(Long orderId);

    List<Review> findByStatus(ReviewStatus status);

    Page<Review> findByStatus(ReviewStatus status, Pageable pageable);

    // === COMPLEX QUERIES ===

    // 1. Tìm review theo court với status
    @Query("""
                SELECT r FROM Review r
                WHERE r.court.id = :courtId
                AND r.status = :status
                AND r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    Page<Review> findByCourtIdAndStatus(
            @Param("courtId") Long courtId,
            @Param("status") ReviewStatus status,
            Pageable pageable);

    // 2. Tìm review theo product với status
    @Query("""
                SELECT r FROM Review r
                WHERE r.product.id = :productId
                AND r.status = :status
                AND r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    Page<Review> findByProductIdAndStatus(
            @Param("productId") Long productId,
            @Param("status") ReviewStatus status,
            Pageable pageable);

    // 3. Tính rating trung bình cho court
    @Query("""
                SELECT AVG(r.rating)
                FROM Review r
                WHERE r.court.id = :courtId
                AND r.status = 'APPROVED'
                AND r.deletedAt IS NULL
            """)
    Double getAverageRatingForCourt(@Param("courtId") Long courtId);

    // 4. Tính rating trung bình cho product
    @Query("""
                SELECT AVG(r.rating)
                FROM Review r
                WHERE r.product.id = :productId
                AND r.status = 'APPROVED'
                AND r.deletedAt IS NULL
            """)
    Double getAverageRatingForProduct(@Param("productId") Long productId);

    // 5. Đếm review theo rating cho court
    @Query("""
                SELECT r.rating, COUNT(r)
                FROM Review r
                WHERE r.court.id = :courtId
                AND r.status = 'APPROVED'
                AND r.deletedAt IS NULL
                GROUP BY r.rating
                ORDER BY r.rating DESC
            """)
    List<Object[]> getRatingDistributionForCourt(@Param("courtId") Long courtId);

    // 6. Đếm review theo rating cho product
    @Query("""
                SELECT r.rating, COUNT(r)
                FROM Review r
                WHERE r.product.id = :productId
                AND r.status = 'APPROVED'
                AND r.deletedAt IS NULL
                GROUP BY r.rating
                ORDER BY r.rating DESC
            """)
    List<Object[]> getRatingDistributionForProduct(@Param("productId") Long productId);

    // 7. Tìm review verified purchase
    @Query("""
                SELECT r FROM Review r
                WHERE r.isVerifiedPurchase = true
                AND r.status = 'APPROVED'
                AND (:courtId IS NULL OR r.court.id = :courtId)
                AND (:productId IS NULL OR r.product.id = :productId)
                AND r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    Page<Review> findVerifiedPurchaseReviews(
            @Param("courtId") Long courtId,
            @Param("productId") Long productId,
            Pageable pageable);

    // 8. Tìm review featured
    @Query("""
                SELECT r FROM Review r
                WHERE r.isFeatured = true
                AND r.status = 'APPROVED'
                AND r.deletedAt IS NULL
                ORDER BY r.helpfulCount DESC, r.createdAt DESC
            """)
    List<Review> findFeaturedReviews(Pageable pageable);

    // 9. Tìm review cần moderate
    @Query("""
                SELECT r FROM Review r
                WHERE r.status = 'PENDING'
                AND r.deletedAt IS NULL
                ORDER BY r.createdAt ASC
            """)
    Page<Review> findReviewsForModeration(Pageable pageable);

    // 10. Tìm review bị report
    @Query("""
                SELECT r FROM Review r
                WHERE r.isReported = true
                AND r.status = 'APPROVED'
                AND r.deletedAt IS NULL
                ORDER BY r.reportCount DESC, r.createdAt DESC
            """)
    Page<Review> findReportedReviews(Pageable pageable);

    // 11. Tìm review theo user với filter
    @Query("""
                SELECT r FROM Review r
                WHERE r.user.id = :userId
                AND (:status IS NULL OR r.status = :status)
                AND (:reviewType IS NULL OR r.reviewType = :reviewType)
                AND r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    Page<Review> findByUserIdWithFilters(
            @Param("userId") Long userId,
            @Param("status") ReviewStatus status,
            @Param("reviewType") ReviewType reviewType,
            Pageable pageable);

    // 12. Tìm review helpful nhất
    @Query("""
                SELECT r FROM Review r
                WHERE r.status = 'APPROVED'
                AND (:courtId IS NULL OR r.court.id = :courtId)
                AND (:productId IS NULL OR r.product.id = :productId)
                AND r.deletedAt IS NULL
                ORDER BY (r.helpfulCount - r.notHelpfulCount) DESC, r.createdAt DESC
            """)
    List<Review> findMostHelpfulReviews(
            @Param("courtId") Long courtId,
            @Param("productId") Long productId,
            Pageable pageable);

    // 13. Tìm review gần đây
    @Query("""
                SELECT r FROM Review r
                WHERE r.status = 'APPROVED'
                AND r.createdAt >= :since
                AND (:courtId IS NULL OR r.court.id = :courtId)
                AND (:productId IS NULL OR r.product.id = :productId)
                AND r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    List<Review> findRecentReviews(
            @Param("courtId") Long courtId,
            @Param("productId") Long productId,
            @Param("since") LocalDateTime since,
            Pageable pageable);

    // 14. Kiểm tra user đã review chưa
    @Query("""
                SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
                FROM Review r
                WHERE r.user.id = :userId
                AND (
                    (r.court.id = :courtId AND :courtId IS NOT NULL)
                    OR (r.product.id = :productId AND :productId IS NOT NULL)
                    OR (r.booking.id = :bookingId AND :bookingId IS NOT NULL)
                    OR (r.order.id = :orderId AND :orderId IS NOT NULL)
                )
                AND r.deletedAt IS NULL
            """)
    boolean hasUserReviewed(
            @Param("userId") Long userId,
            @Param("courtId") Long courtId,
            @Param("productId") Long productId,
            @Param("bookingId") Long bookingId,
            @Param("orderId") Long orderId);

    // 15. Thống kê review
    @Query("""
                SELECT
                    COUNT(r) as totalReviews,
                    AVG(r.rating) as averageRating,
                    SUM(CASE WHEN r.rating = 5 THEN 1 ELSE 0 END) as fiveStars,
                    SUM(CASE WHEN r.rating = 4 THEN 1 ELSE 0 END) as fourStars,
                    SUM(CASE WHEN r.rating = 3 THEN 1 ELSE 0 END) as threeStars,
                    SUM(CASE WHEN r.rating = 2 THEN 1 ELSE 0 END) as twoStars,
                    SUM(CASE WHEN r.rating = 1 THEN 1 ELSE 0 END) as oneStar
                FROM Review r
                WHERE r.status = 'APPROVED'
                AND (:courtId IS NULL OR r.court.id = :courtId)
                AND (:productId IS NULL OR r.product.id = :productId)
                AND r.deletedAt IS NULL
            """)
    Object[] getReviewStatistics(
            @Param("courtId") Long courtId,
            @Param("productId") Long productId);

    // 16. Tìm kiếm review
    @Query("""
                SELECT r FROM Review r
                WHERE (
                    LOWER(r.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                    OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                )
                AND r.status = 'APPROVED'
                AND r.deletedAt IS NULL
                ORDER BY r.createdAt DESC
            """)
    Page<Review> searchReviews(
            @Param("searchTerm") String searchTerm,
            Pageable pageable);
}
