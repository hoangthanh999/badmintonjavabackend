
package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.promotion.PromotionDTO;
import com.hoangthanhhong.badminton.entity.Promotion;
import org.springframework.stereotype.Component;

@Component
public class PromotionMapper {

    public PromotionDTO toDTO(Promotion promotion) {
        if (promotion == null)
            return null;

        return PromotionDTO.builder()
                .id(promotion.getId())
                .code(promotion.getCode())
                .name(promotion.getName())
                .description(promotion.getDescription())
                .discountType(promotion.getDiscountType())
                .discountValue(promotion.getDiscountValue())
                .maxDiscountAmount(promotion.getMaxDiscountAmount())
                .minOrderAmount(promotion.getMinOrderAmount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .status(promotion.getStatus())
                .maxUsage(promotion.getMaxUsage())
                .currentUsage(promotion.getCurrentUsage())
                .remainingUsage(promotion.getRemainingUsage())
                .isPublic(promotion.getIsPublic())
                .imageUrl(promotion.getImageUrl())
                .build();
    }
}
