package com.hoangthanhhong.badminton.dto.request.promotion;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePromotionRequest {
    private String name;
    private String description;
    private Double discountValue;
    private Double maxDiscountAmount;
    private Double minOrderAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer maxUsage;
    private Integer maxUsagePerUser;
    private String imageUrl;
    private String termsAndConditions;
}
