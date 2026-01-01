package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.ReviewHelpful;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ReviewHelpfulRepository extends JpaRepository<ReviewHelpful, Long> {

    Optional<ReviewHelpful> findByReviewIdAndUserId(Long reviewId, Long userId);

    boolean existsByReviewIdAndUserId(Long reviewId, Long userId);

    void deleteByReviewIdAndUserId(Long reviewId, Long userId);

    @Query("""
                SELECT COUNT(rh)
                FROM ReviewHelpful rh
                WHERE rh.review.id = :reviewId
                AND rh.isHelpful = true
            """)
    Long countHelpfulByReviewId(@Param("reviewId") Long reviewId);

    @Query("""
                SELECT COUNT(rh)
                FROM ReviewHelpful rh
                WHERE rh.review.id = :reviewId
                AND rh.isHelpful = false
            """)
    Long countNotHelpfulByReviewId(@Param("reviewId") Long reviewId);
}
