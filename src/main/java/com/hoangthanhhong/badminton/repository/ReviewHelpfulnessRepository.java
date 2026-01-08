// File: ReviewHelpfulnessRepository.java (TẠO MỚI)
package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.ReviewHelpful;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewHelpfulnessRepository extends JpaRepository<ReviewHelpful, Long> {

    Optional<ReviewHelpful> findByReviewIdAndUserId(Long reviewId, Long userId);

    void deleteByReviewIdAndUserId(Long reviewId, Long userId);
}
