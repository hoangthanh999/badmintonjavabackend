package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.request.UserReferralRequest;
import com.hoangthanhhong.badminton.dto.response.UserReferralResponse;
import com.hoangthanhhong.badminton.entity.User;
import com.hoangthanhhong.badminton.entity.UserReferral;
import com.hoangthanhhong.badminton.enums.ReferralStatus;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.UserReferralMapper;
import com.hoangthanhhong.badminton.repository.UserReferralRepository;
import com.hoangthanhhong.badminton.repository.UserRepository;
import com.hoangthanhhong.badminton.service.UserReferralService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserReferralServiceImpl implements UserReferralService {

    private final UserReferralRepository referralRepository;
    private final UserRepository userRepository;
    private final UserReferralMapper referralMapper;

    private static final int REFERRAL_CODE_LENGTH = 8;
    private static final String REFERRAL_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Override
    public UserReferralResponse createReferral(UserReferralRequest request, Long referrerId) {
        User referrer = userRepository.findById(referrerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        String referralCode = generateUniqueReferralCode(referrerId);

        UserReferral referral = UserReferral.builder()
                .referrer(referrer)
                .referralCode(referralCode)
                .referredEmail(request.getReferredEmail())
                .referredPhone(request.getReferredPhone())
                .status(ReferralStatus.PENDING)
                .expiredAt(LocalDateTime.now().plusDays(30)) // Hết hạn sau 30 ngày
                .campaignId(request.getCampaignId())
                .utmSource(request.getUtmSource())
                .utmMedium(request.getUtmMedium())
                .utmCampaign(request.getUtmCampaign())
                .notes(request.getNotes())
                .build();

        referral = referralRepository.save(referral);
        log.info("Created referral with code: {} for user: {}", referralCode, referrerId);

        return referralMapper.toResponse(referral);
    }

    @Override
    public String generateUniqueReferralCode(Long userId) {
        String code;
        do {
            code = generateRandomCode();
        } while (referralRepository.existsByReferralCode(code));
        return code;
    }

    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < REFERRAL_CODE_LENGTH; i++) {
            code.append(REFERRAL_CODE_CHARS.charAt(random.nextInt(REFERRAL_CODE_CHARS.length())));
        }
        return code.toString();
    }

    @Override
    @Transactional(readOnly = true)
    public UserReferralResponse getReferralById(Long id) {
        UserReferral referral = referralRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy referral"));
        return referralMapper.toResponse(referral);
    }

    @Override
    @Transactional(readOnly = true)
    public UserReferralResponse getReferralByCode(String code) {
        UserReferral referral = referralRepository.findByReferralCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Mã giới thiệu không hợp lệ"));
        return referralMapper.toResponse(referral);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserReferralResponse> getReferralsByReferrerId(Long referrerId) {
        return referralRepository.findByReferrerId(referrerId).stream()
                .map(referralMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserReferralResponse> getReferralsByReferrerId(Long referrerId, Pageable pageable) {
        return referralRepository.findByReferrerId(referrerId, pageable)
                .map(referralMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserReferralResponse> getReferralsByStatus(ReferralStatus status) {
        return referralRepository.findByStatus(status).stream()
                .map(referralMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserReferralResponse updateReferralStatus(Long id, ReferralStatus status) {
        UserReferral referral = referralRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy referral"));

        referral.setStatus(status);
        referral = referralRepository.save(referral);

        log.info("Updated referral {} status to: {}", id, status);
        return referralMapper.toResponse(referral);
    }

    @Override
    public UserReferralResponse markAsRegistered(String referralCode, Long referredUserId) {
        UserReferral referral = referralRepository.findByReferralCode(referralCode)
                .orElseThrow(() -> new ResourceNotFoundException("Mã giới thiệu không hợp lệ"));

        User referred = userRepository.findById(referredUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        referral.markAsRegistered(referred);
        referral = referralRepository.save(referral);

        log.info("Marked referral {} as registered for user: {}", referralCode, referredUserId);
        return referralMapper.toResponse(referral);
    }

    @Override
    public UserReferralResponse markAsCompleted(Long id, Integer points, Double reward) {
        UserReferral referral = referralRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy referral"));

        if (referral.getStatus() != ReferralStatus.REGISTERED) {
            throw new IllegalStateException("Chỉ có thể hoàn thành referral đã đăng ký");
        }

        referral.markAsCompleted(points, reward);
        referral = referralRepository.save(referral);

        log.info("Completed referral {} with {} points", id, points);
        return referralMapper.toResponse(referral);
    }

    @Override
    public UserReferralResponse claimReward(Long id, Long userId) {
        UserReferral referral = referralRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy referral"));

        if (!referral.getReferrer().getId().equals(userId)) {
            throw new IllegalStateException("Bạn không có quyền claim referral này");
        }

        if (!referral.canClaimReward()) {
            throw new IllegalStateException("Referral không thể claim");
        }

        referral.claimReward();
        referral = referralRepository.save(referral);

        log.info("User {} claimed reward for referral {}", userId, id);
        return referralMapper.toResponse(referral);
    }

    @Override
    public void deleteReferral(Long id) {
        UserReferral referral = referralRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy referral"));
        referral.softDelete();
        referralRepository.save(referral);
        log.info("Soft deleted referral: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReferralsByUser(Long userId) {
        return referralRepository.countByReferrerId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countSuccessfulReferralsByUser(Long userId) {
        return referralRepository.countSuccessfulReferralsByReferrerId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getTotalPointsEarned(Long userId) {
        Integer total = referralRepository.getTotalPointsEarnedByReferrerId(userId);
        return total != null ? total : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTopReferrers(int limit) {
        return referralRepository.findTopReferrers(PageRequest.of(0, limit));
    }

    @Override
    @Transactional(readOnly = true)
    public Object[] getReferralStatistics() {
        return referralRepository.getReferralStatistics();
    }

    @Override
    public void processExpiredReferrals() {
        List<UserReferral> expiredReferrals = referralRepository.findExpiredReferrals(LocalDateTime.now());

        for (UserReferral referral : expiredReferrals) {
            referral.markAsExpired();
        }

        if (!expiredReferrals.isEmpty()) {
            referralRepository.saveAll(expiredReferrals);
            log.info("Processed {} expired referrals", expiredReferrals.size());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateReferralCode(String code) {
        return referralRepository.findByReferralCode(code)
                .map(r -> r.getStatus() == ReferralStatus.PENDING && !r.getIsExpired())
                .orElse(false);
    }

    @Override
    public UserReferralResponse registerWithReferralCode(String code, String email) {
        UserReferral referral = referralRepository.findByReferralCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Mã giới thiệu không hợp lệ"));

        if (referral.getStatus() != ReferralStatus.PENDING) {
            throw new IllegalStateException("Mã giới thiệu đã được sử dụng");
        }

        if (referral.getIsExpired()) {
            throw new IllegalStateException("Mã giới thiệu đã hết hạn");
        }

        referral.setReferredEmail(email);
        referral = referralRepository.save(referral);

        return referralMapper.toResponse(referral);
    }
}