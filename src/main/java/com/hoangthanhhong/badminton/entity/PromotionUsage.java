package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "promotion_usages", indexes = {
        @Index(name = "idx_promotion_id", columnList = "promotion_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_used_at", columnList = "used_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionUsage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    @Column(name = "original_amount", nullable = false)
    private Double originalAmount;

    @Column(name = "discount_amount", nullable = false)
    private Double discountAmount;

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;

    @Column(name = "is_reverted")
    @Builder.Default
    private Boolean isReverted = false;

    @Column(name = "reverted_at")
    private LocalDateTime revertedAt;

    @Column(name = "revert_reason", columnDefinition = "TEXT")
    private String revertReason;

    @PrePersist
    public void setUsedAt() {
        if (usedAt == null) {
            usedAt = LocalDateTime.now();
        }
    }

    public void revert(String reason) {
        this.isReverted = true;
        this.revertedAt = LocalDateTime.now();
        this.revertReason = reason;
    }
}
