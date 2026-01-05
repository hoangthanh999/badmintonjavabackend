
package com.hoangthanhhong.badminton.dto.request.promotion;

import com.hoangthanhhong.badminton.enums.DiscountType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePromotionRequest {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private DiscountType discountType;
    @NotNull
    private Double discountValue;
    private Double maxDiscountAmount;
    private Double minOrderAmount;
    @NotNull
    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    private Integer maxUsage;
    private Integer maxUsagePerUser;
    private Boolean isPublic;
    private Boolean isAutoApply;
    private String imageUrl;
    private String termsAndConditions;
    private String applicableTo;
    private String applicableDays;
    private LocalTime applicableTimeStart;
    private LocalTime applicableTimeEnd;
}
