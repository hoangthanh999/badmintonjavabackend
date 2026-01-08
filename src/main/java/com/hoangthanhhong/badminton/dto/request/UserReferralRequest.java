package com.hoangthanhhong.badminton.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserReferralRequest {

    @Email(message = "Email không hợp lệ")
    private String referredEmail;

    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String referredPhone;

    private Long campaignId;

    private String utmSource;

    private String utmMedium;

    private String utmCampaign;

    private String notes;
}