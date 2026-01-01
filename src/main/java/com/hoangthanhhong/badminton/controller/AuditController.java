package com.hoangthanhhong.badminton.controller;

import com.hoangthanhhong.badminton.dto.audit.AuditLogDTO;
import com.hoangthanhhong.badminton.dto.audit.AuditStatisticsDTO;
import com.hoangthanhhong.badminton.dto.response.ApiResponse;
import com.hoangthanhhong.badminton.security.UserPrincipal;
import com.hoangthanhhong.badminton.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit", description = "Audit Log APIs")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/{id}")
    @Operation(summary = "Get audit log by ID", description = "Get audit log details by ID")
    public ResponseEntity<ApiResponse<AuditLogDTO>> getAuditLogById(@PathVariable Long id) {
        AuditLogDTO auditLog = auditService.getAuditLogById(id);

        return ResponseEntity.ok(ApiResponse.success(auditLog));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user audit logs", description = "Get all audit logs for a specific user")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getUserAuditLogs(
            @PathVariable Long userId,
            Pageable pageable) {

        Page<AuditLogDTO> auditLogs = auditService.getUserAuditLogs(userId, pageable);

        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'USER')")
    @Operation(summary = "Get my audit logs", description = "Get audit logs for current user")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getMyAuditLogs(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            Pageable pageable) {

        Page<AuditLogDTO> auditLogs = auditService.getUserAuditLogs(
                userPrincipal.getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @Operation(summary = "Get entity audit logs", description = "Get all audit logs for a specific entity")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getEntityAuditLogs(
            @PathVariable String entityType,
            @PathVariable Long entityId,
            Pageable pageable) {

        Page<AuditLogDTO> auditLogs = auditService.getEntityAuditLogs(
                entityType, entityId, pageable);

        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get audit statistics", description = "Get audit statistics for a date range")
    public ResponseEntity<ApiResponse<AuditStatisticsDTO>> getAuditStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        AuditStatisticsDTO statistics = auditService.getAuditStatistics(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/search")
    @Operation(summary = "Search audit logs", description = "Search audit logs with filters")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> searchAuditLogs(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {

        Page<AuditLogDTO> auditLogs = auditService.searchAuditLogs(
                searchTerm, action, status, startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.success(auditLogs));
    }

    @PostMapping("/{id}/flag")
    @Operation(summary = "Flag audit log", description = "Flag an audit log for review")
    public ResponseEntity<ApiResponse<Void>> flagLog(
            @PathVariable Long id,
            @RequestParam String reason) {

        auditService.flagLog(id, reason);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Audit log flagged successfully"));
    }
}
