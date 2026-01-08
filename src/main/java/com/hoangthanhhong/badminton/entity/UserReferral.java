package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.ReferralStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_referrals", indexes = {
        @Index(name = "idx_referrer_id", columnList = "referrer_id"),
        @Index(name = "idx_referred_id", columnList = "referred_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_referral_code", columnList = "referral_code")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_referrer_referred", columnNames = { "referrer_id", "referred_id" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReferral extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_id", nullable = false)
    private User referrer; // Người giới thiệu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_id")
    private User referred; // Người được giới thiệu

    @Column(name = "referral_code", length = 50, unique = true, nullable = false)
    private String referralCode;

    @Column(name = "referred_email", length = 100)
    private String referredEmail; // Email của người được mời (trước khi đăng ký)

    @Column(name = "referred_phone", length = 20)
    private String referredPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private ReferralStatus status = ReferralStatus.PENDING;

    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;

    @Column(name = "reward_amount")
    private Double rewardAmount;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt; // Thời điểm người được giới thiệu đăng ký

    @Column(name = "completed_at")
    private LocalDateTime completedAt; // Thời điểm hoàn thành (đủ điều kiện nhận thưởng)

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "is_expired")
    @Builder.Default
    private Boolean isExpired = false;

    @Column(name = "reward_claimed")
    @Builder.Default
    private Boolean rewardClaimed = false;

    @Column(name = "reward_claimed_at")
    private LocalDateTime rewardClaimedAt;

    @Column(name = "first_booking_id")
    private Long firstBookingId; // ID booking đầu tiên của người được giới thiệu

    @Column(name = "first_order_id")
    private Long firstOrderId; // ID order đầu tiên

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "campaign_id")
    private Long campaignId; // Nếu có chiến dịch referral

    @Column(name = "utm_source", length = 100)
    private String utmSource;

    @Column(name = "utm_medium", length = 100)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 100)
    private String utmCampaign;

    // === HELPER METHODS ===

    public void markAsRegistered(User referred) {
        this.referred = referred;
        this.status = ReferralStatus.REGISTERED;
        this.registeredAt = LocalDateTime.now();
    }

    public void markAsCompleted(Integer points, Double reward) {
        this.status = ReferralStatus.COMPLETED;
        this.pointsEarned = points;
        this.rewardAmount = reward;
        this.completedAt = LocalDateTime.now();
    }

    public void claimReward() {
        if (!this.rewardClaimed && this.status == ReferralStatus.COMPLETED) {
            this.rewardClaimed = true;
            this.rewardClaimedAt = LocalDateTime.now();
        }
    }

    public void markAsExpired() {
        this.isExpired = true;
        this.status = ReferralStatus.EXPIRED;
        this.expiredAt = LocalDateTime.now();
    }

    public boolean canClaimReward() {
        return status == ReferralStatus.COMPLETED && !rewardClaimed && !isExpired;
    }
}