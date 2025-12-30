package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.promotion.PromotionDTO;
import com.hoangthanhhong.badminton.dto.request.promotion.CreatePromotionRequest;
import com.hoangthanhhong.badminton.dto.request.promotion.UpdatePromotionRequest;
import com.hoangthanhhong.badminton.dto.request.promotion.ValidatePromotionRequest;
import com.hoangthanhhong.badminton.dto.response.promotion.PromotionValidationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PromotionService {

    PromotionDTO createPromotion(CreatePromotionRequest request);

    PromotionDTO updatePromotion(Long id, UpdatePromotionRequest request);

    void deletePromotion(Long id);

    PromotionDTO getPromotionById(Long id);

    PromotionDTO getPromotionByCode(String code);

    Page<PromotionDTO> getAllPromotions(Pageable pageable);

    List<PromotionDTO> getActivePromotions();

    List<PromotionDTO> getAvailablePromotionsForUser(Long userId);

    PromotionValidationResponse validatePromotion(ValidatePromotionRequest request);

    Double calculateDiscount(String code, Double amount);

    void usePromotion(Long promotionId, Long userId, Long bookingId, Long orderId, Double amount);

    void revertPromotionUsage(Long usageId, String reason);

    void activatePromotion(Long id);

    void deactivatePromotion(Long id);

    List<PromotionDTO> searchPromotions(String searchTerm, String status, Pageable pageable);
}
