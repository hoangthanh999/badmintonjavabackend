package com.hoangthanhhong.badminton.dto.request.promotion;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidatePromotionRequest {
    private String code;
    private Long userId;
    private Double amount;
}