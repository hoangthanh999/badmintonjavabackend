package com.hoangthanhhong.badminton.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hoangthanhhong.badminton.enums.ReferralStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReferralResponse {

    private Long id;

    private Long referrerId;

    private String referrerName;

    private String referrerEmail;

    private Long referredId;

    private String referredName;

    private String referredEmail;

    private String referredPhone;

    private String referralCode;

    private ReferralStatus status;

    private Integer pointsEarned;

    private Double rewardAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registeredAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime completedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiredAt;

    private Boolean isExpired;

    private Boolean rewardClaimed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rewardClaimedAt;

    private Long firstBookingId;

    private Long firstOrderId;

    private String notes;

    private Long campaignId;

    private String utmSource;

    private String utmMedium;

    private String utmCampaign;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}