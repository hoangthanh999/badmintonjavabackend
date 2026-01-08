package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.request.UserReferralRequest;
import com.hoangthanhhong.badminton.dto.response.UserReferralResponse;
import com.hoangthanhhong.badminton.entity.UserReferral;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserReferralMapper {

    @Mapping(target = "referrerId", source = "referrer.id")
    @Mapping(target = "referrerName", source = "referrer.name")
    @Mapping(target = "referrerEmail", source = "referrer.email")
    @Mapping(target = "referredId", source = "referred.id")
    @Mapping(target = "referredName", source = "referred.name")
    @Mapping(target = "referredEmail", source = "referredEmail")
    UserReferralResponse toResponse(UserReferral referral);

    List<UserReferralResponse> toResponseList(List<UserReferral> referrals);

    @Mapping(target = "referrer", ignore = true)
    @Mapping(target = "referred", ignore = true)
    @Mapping(target = "referralCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "pointsEarned", ignore = true)
    @Mapping(target = "rewardAmount", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "completedAt", ignore = true)
    @Mapping(target = "expiredAt", ignore = true)
    @Mapping(target = "isExpired", ignore = true)
    @Mapping(target = "rewardClaimed", ignore = true)
    @Mapping(target = "rewardClaimedAt", ignore = true)
    @Mapping(target = "firstBookingId", ignore = true)
    @Mapping(target = "firstOrderId", ignore = true)
    UserReferral toEntity(UserReferralRequest request);
}