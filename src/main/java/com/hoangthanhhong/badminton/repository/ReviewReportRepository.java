package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.ReviewReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {

    List<ReviewReport> findByReviewId(Long reviewId);

    List<ReviewReport> findByReporterId(Long reporterId);

    List<ReviewReport> findByStatus(String status);

    Page<ReviewReport> findByStatus(String status, Pageable pageable);

    @Query("""
                SELECT COUNT(rr)
                FROM ReviewReport rr
                WHERE rr.review.id = :reviewId
                AND rr.status = 'PENDING'
            """)
    Long countPendingReportsByReviewId(@Param("reviewId") Long reviewId);

    boolean existsByReviewIdAndReporterId(Long reviewId, Long reporterId);
}
