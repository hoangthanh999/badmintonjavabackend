package com.hoangthanhhong.badminton.controller;

import com.hoangthanhhong.badminton.dto.request.UserReferralRequest;
import com.hoangthanhhong.badminton.dto.response.UserReferralResponse;
import com.hoangthanhhong.badminton.enums.ReferralStatus;
import com.hoangthanhhong.badminton.service.UserReferralService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/referrals")
@RequiredArgsConstructor
public class UserReferralController {

    private final UserReferralService referralService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserReferralResponse> createReferral(
            @Valid @RequestBody UserReferralRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserReferralResponse response = referralService.createReferral(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserReferralResponse> getReferral(@PathVariable Long id) {
        UserReferralResponse response = referralService.getReferralById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<UserReferralResponse> getReferralByCode(@PathVariable String code) {
        UserReferralResponse response = referralService.getReferralByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-referrals")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserReferralResponse>> getMyReferrals(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<UserReferralResponse> referrals = referralService.getReferralsByReferrerId(userId);
        return ResponseEntity.ok(referrals);
    }

    @GetMapping("/my-referrals/page")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<UserReferralResponse>> getMyReferralsPage(
            Authentication authentication,
            Pageable pageable) {
        Long userId = getUserIdFromAuth(authentication);
        Page<UserReferralResponse> referrals = referralService.getReferralsByReferrerId(userId, pageable);
        return ResponseEntity.ok(referrals);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserReferralResponse>> getReferralsByStatus(@PathVariable ReferralStatus status) {
        List<UserReferralResponse> referrals = referralService.getReferralsByStatus(status);
        return ResponseEntity.ok(referrals);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserReferralResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam ReferralStatus status) {
        UserReferralResponse response = referralService.updateReferralStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserReferralResponse> markAsCompleted(
            @PathVariable Long id,
            @RequestParam Integer points,
            @RequestParam(required = false) Double reward) {
        UserReferralResponse response = referralService.markAsCompleted(id, points, reward);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/claim")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserReferralResponse> claimReward(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserReferralResponse response = referralService.claimReward(id, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteReferral(@PathVariable Long id) {
        referralService.deleteReferral(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-referrals/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyReferralStats(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReferrals", referralService.countReferralsByUser(userId));
        stats.put("successfulReferrals", referralService.countSuccessfulReferralsByUser(userId));
        stats.put("totalPointsEarned", referralService.getTotalPointsEarned(userId));

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Map<String, Boolean>> validateReferralCode(@PathVariable String code) {
        boolean isValid = referralService.validateReferralCode(code);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }

    @PostMapping("/register/{code}")
    public ResponseEntity<UserReferralResponse> registerWithReferralCode(
            @PathVariable String code,
            @RequestParam String email) {
        UserReferralResponse response = referralService.registerWithReferralCode(code, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/top-referrers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Object[]>> getTopReferrers(@RequestParam(defaultValue = "10") int limit) {
        List<Object[]> topReferrers = referralService.getTopReferrers(limit);
        return ResponseEntity.ok(topReferrers);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object[]> getReferralStatistics() {
        Object[] stats = referralService.getReferralStatistics();
        return ResponseEntity.ok(stats);
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}