package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_helpful", uniqueConstraints = @UniqueConstraint(columnNames = { "review_id",
        "user_id" }), indexes = {
                @Index(name = "idx_review_id", columnList = "review_id"),
                @Index(name = "idx_user_id", columnList = "user_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewHelpful extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_helpful", nullable = false)
    private Boolean isHelpful; // true = helpful, false = not helpful

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    @PrePersist
    public void setVotedAt() {
        if (votedAt == null) {
            votedAt = LocalDateTime.now();
        }
    }
}
