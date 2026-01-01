package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_reports", indexes = {
        @Index(name = "idx_review_id", columnList = "review_id"),
        @Index(name = "idx_reporter_id", columnList = "reporter_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewReport extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(nullable = false, length = 50)
    private String reason; // SPAM, OFFENSIVE, FAKE, INAPPROPRIATE, OTHER

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING"; // PENDING, REVIEWED, RESOLVED, DISMISSED

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @PrePersist
    public void setReportedAt() {
        if (reportedAt == null) {
            reportedAt = LocalDateTime.now();
        }
    }

    public void resolve(Long reviewedBy, String notes) {
        this.status = "RESOLVED";
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = reviewedBy;
        this.resolutionNotes = notes;
    }

    public void dismiss(Long reviewedBy, String notes) {
        this.status = "DISMISSED";
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = reviewedBy;
        this.resolutionNotes = notes;
    }
}
