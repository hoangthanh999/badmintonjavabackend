package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.promotion.PromotionDTO;
import com.hoangthanhhong.badminton.dto.request.promotion.CreatePromotionRequest;
import com.hoangthanhhong.badminton.dto.request.promotion.UpdatePromotionRequest;
import com.hoangthanhhong.badminton.dto.request.promotion.ValidatePromotionRequest;
import com.hoangthanhhong.badminton.dto.response.promotion.PromotionValidationResponse;
import com.hoangthanhhong.badminton.entity.*;
import com.hoangthanhhong.badminton.enums.PromotionStatus;
import com.hoangthanhhong.badminton.exception.BadRequestException;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.PromotionMapper;
import com.hoangthanhhong.badminton.repository.*;
import com.hoangthanhhong.badminton.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionUsageRepository promotionUsageRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final OrderRepository orderRepository;
    private final PromotionMapper promotionMapper;

    @Override
    public PromotionDTO createPromotion(CreatePromotionRequest request) {
        // Check if code already exists
        if (promotionRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Promotion code already exists");
        }

        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        Promotion promotion = Promotion.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .maxDiscountAmount(request.getMaxDiscountAmount())
                .minOrderAmount(request.getMinOrderAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(PromotionStatus.ACTIVE)
                .maxUsage(request.getMaxUsage())
                .maxUsagePerUser(request.getMaxUsagePerUser())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : true)
                .isAutoApply(request.getIsAutoApply() != null ? request.getIsAutoApply() : false)
                .imageUrl(request.getImageUrl())
                .termsAndConditions(request.getTermsAndConditions())
                .applicableTo(request.getApplicableTo())
                .applicableDays(request.getApplicableDays())
                .applicableTimeStart(request.getApplicableTimeStart())
                .applicableTimeEnd(request.getApplicableTimeEnd())
                .build();

        promotion = promotionRepository.save(promotion);

        log.info("Created promotion: {}", promotion.getCode());
        return promotionMapper.toDTO(promotion);
    }

    @Override
    public PromotionDTO updatePromotion(Long id, UpdatePromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        if (request.getName() != null) {
            promotion.setName(request.getName());
        }
        if (request.getDescription() != null) {
            promotion.setDescription(request.getDescription());
        }
        if (request.getDiscountValue() != null) {
            promotion.setDiscountValue(request.getDiscountValue());
        }
        if (request.getMaxDiscountAmount() != null) {
            promotion.setMaxDiscountAmount(request.getMaxDiscountAmount());
        }
        if (request.getMinOrderAmount() != null) {
            promotion.setMinOrderAmount(request.getMinOrderAmount());
        }
        if (request.getStartDate() != null) {
            promotion.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            promotion.setEndDate(request.getEndDate());
        }
        if (request.getMaxUsage() != null) {
            promotion.setMaxUsage(request.getMaxUsage());
        }
        if (request.getMaxUsagePerUser() != null) {
            promotion.setMaxUsagePerUser(request.getMaxUsagePerUser());
        }
        if (request.getImageUrl() != null) {
            promotion.setImageUrl(request.getImageUrl());
        }
        if (request.getTermsAndConditions() != null) {
            promotion.setTermsAndConditions(request.getTermsAndConditions());
        }

        promotion = promotionRepository.save(promotion);

        log.info("Updated promotion: {}", promotion.getCode());
        return promotionMapper.toDTO(promotion);
    }

    @Override
    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        promotion.softDelete();
        promotionRepository.save(promotion);

        log.info("Deleted promotion: {}", promotion.getCode());
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionDTO getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        return promotionMapper.toDTO(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionDTO getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        return promotionMapper.toDTO(promotion);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PromotionDTO> getAllPromotions(Pageable pageable) {
        Page<Promotion> promotions = promotionRepository.findAll(pageable);
        return promotions.map(promotionMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getActivePromotions() {
        List<Promotion> promotions = promotionRepository.findActivePromotions(LocalDate.now());
        return promotions.stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> getAvailablePromotionsForUser(Long userId) {
        List<Promotion> promotions = promotionRepository.findAvailablePromotionsForUser(
                userId, LocalDate.now());
        return promotions.stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionValidationResponse validatePromotion(ValidatePromotionRequest request) {
        Promotion promotion = promotionRepository.findByCode(request.getCode().toUpperCase())
                .orElse(null);

        if (promotion == null) {
            return PromotionValidationResponse.builder()
                    .valid(false)
                    .message("Promotion code not found")
                    .build();
        }

        // Check if active
        if (!promotion.isActive()) {
            return PromotionValidationResponse.builder()
                    .valid(false)
                    .message("Promotion is not active")
                    .build();
        }

        // Check if user can use
        if (!promotion.canBeUsedBy(request.getUserId())) {
            return PromotionValidationResponse.builder()
                    .valid(false)
                    .message("You cannot use this promotion")
                    .build();
        }

        // Check minimum amount
        if (promotion.getMinOrderAmount() != null &&
                request.getAmount() < promotion.getMinOrderAmount()) {
            return PromotionValidationResponse.builder()
                    .valid(false)
                    .message(String.format("Minimum order amount is %.0f VND",
                            promotion.getMinOrderAmount()))
                    .build();
        }

        // Calculate discount
        Double discount = promotion.calculateDiscount(request.getAmount());

        return PromotionValidationResponse.builder()
                .valid(true)
                .message("Promotion is valid")
                .promotionId(promotion.getId())
                .promotionCode(promotion.getCode())
                .promotionName(promotion.getName())
                .discountAmount(discount)
                .finalAmount(request.getAmount() - discount)
                .build();
    }

    @Override
    public Double calculateDiscount(String code, Double amount) {
        Promotion promotion = promotionRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        return promotion.calculateDiscount(amount);
    }

    @Override
    public void usePromotion(Long promotionId, Long userId, Long bookingId, Long orderId, Double amount) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Validate
        if (!promotion.canBeUsedBy(userId)) {
            throw new BadRequestException("Cannot use this promotion");
        }

        // Calculate discount
        Double discount = promotion.calculateDiscount(amount);

        // Create usage record
        PromotionUsage usage = PromotionUsage.builder()
                .promotion(promotion)
                .user(user)
                .originalAmount(amount)
                .discountAmount(discount)
                .finalAmount(amount - discount)
                .build();

        if (bookingId != null) {
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
            usage.setBooking(booking);
        }

        if (orderId != null) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
            usage.setOrder(order);
        }

        promotionUsageRepository.save(usage);

        // Increment usage
        promotion.incrementUsage();
        promotionRepository.save(promotion);

        log.info("Promotion {} used by user {}", promotion.getCode(), userId);
    }

    @Override
    public void revertPromotionUsage(Long usageId, String reason) {
        PromotionUsage usage = promotionUsageRepository.findById(usageId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion usage not found"));

        if (usage.getIsReverted()) {
            throw new BadRequestException("Promotion usage already reverted");
        }

        usage.revert(reason);
        promotionUsageRepository.save(usage);

        // Decrement usage
        Promotion promotion = usage.getPromotion();
        promotion.decrementUsage();
        promotionRepository.save(promotion);

        log.info("Reverted promotion usage: {}", usageId);
    }

    @Override
    public void activatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        promotion.activate();
        promotionRepository.save(promotion);

        log.info("Activated promotion: {}", promotion.getCode());
    }

    @Override
    public void deactivatePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));

        promotion.deactivate();
        promotionRepository.save(promotion);

        log.info("Deactivated promotion: {}", promotion.getCode());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionDTO> searchPromotions(String searchTerm, String status, Pageable pageable) {
        PromotionStatus promotionStatus = status != null ? PromotionStatus.valueOf(status) : null;
        Page<Promotion> promotions = promotionRepository.searchPromotions(
                searchTerm, promotionStatus, pageable);
        return promotions.stream()
                .map(promotionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
