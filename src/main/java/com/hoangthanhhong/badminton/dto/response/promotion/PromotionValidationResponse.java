package com.hoangthanhhong.badminton.dto.response.promotion;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionValidationResponse {
    private Boolean valid;
    private String message;
    private Long promotionId;
    private String promotionCode;
    private String promotionName;
    private Double discountAmount;
    private Double finalAmount;
}
