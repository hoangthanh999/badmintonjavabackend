package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.PointTransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_points", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_transaction_type", columnList = "transaction_type"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyPoint extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 50)
    private PointTransactionType transactionType;

    @Column(nullable = false)
    private Integer points;

    @Column(name = "balance_after", nullable = false)
    private Integer balanceAfter;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String reference; // Reference number for transaction

    // === RELATED ENTITIES ===

    @Column(name = "related_entity_type", length = 50)
    private String relatedEntityType; // BOOKING, ORDER, REFERRAL, etc.

    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    // === EXPIRATION ===

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "is_expired")
    @Builder.Default
    private Boolean isExpired = false;

    // === REVERSAL ===

    @Column(name = "is_reversed")
    @Builder.Default
    private Boolean isReversed = false;

    @Column(name = "reversed_at")
    private LocalDateTime reversedAt;

    @Column(name = "reversal_reason", columnDefinition = "TEXT")
    private String reversalReason;

    @Column(name = "reversed_by")
    private Long reversedBy;

    // === HELPER METHODS ===

    public boolean isEarned() {
        return points > 0;
    }

    public boolean isSpent() {
        return points < 0;
    }

    public boolean isExpired() {
        if (isExpired)
            return true;
        if (expiresAt != null && expiresAt.isBefore(LocalDateTime.now())) {
            this.isExpired = true;
            return true;
        }
        return false;
    }

    public void reverse(Long reversedBy, String reason) {
        this.isReversed = true;
        this.reversedAt = LocalDateTime.now();
        this.reversedBy = reversedBy;
        this.reversalReason = reason;
    }

    public void expire() {
        this.isExpired = true;
    }
}
