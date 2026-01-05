package com.hoangthanhhong.badminton.dto.promotion;

import com.hoangthanhhong.badminton.enums.*;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromotionDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private DiscountType discountType;
    private Double discountValue;
    private Double maxDiscountAmount;
    private Double minOrderAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromotionStatus status;
    private Integer maxUsage;
    private Integer currentUsage;
    private Integer remainingUsage;
    private Boolean isPublic;
    private String imageUrl;
}
