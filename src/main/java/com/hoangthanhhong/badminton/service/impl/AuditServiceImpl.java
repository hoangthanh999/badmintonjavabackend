package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.audit.AuditLogDTO;
import com.hoangthanhhong.badminton.dto.audit.AuditStatisticsDTO;
import com.hoangthanhhong.badminton.entity.AuditLog;
import com.hoangthanhhong.badminton.entity.User;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.AuditMapper;
import com.hoangthanhhong.badminton.repository.AuditLogRepository;
import com.hoangthanhhong.badminton.repository.UserRepository;
import com.hoangthanhhong.badminton.service.AuditService;
import com.hoangthanhhong.badminton.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final AuditMapper auditMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void log(String action, String actionCategory, String description,
            Long userId, String entityType, Long entityId,
            Map<String, Object> oldValue, Map<String, Object> newValue) {

        try {
            HttpServletRequest request = getCurrentRequest();

            User user = null;
            if (userId != null) {
                user = userRepository.findById(userId).orElse(null);
            }

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(user != null ? user.getUsername() : null)
                    .userEmail(user != null ? user.getEmail() : null)
                    .userRole(user != null && user.getRole() != null ? user.getRole().getName() : null)
                    .action(action)
                    .actionCategory(actionCategory)
                    .description(description)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValue != null ? JsonUtil.toJson(oldValue) : null)
                    .newValue(newValue != null ? JsonUtil.toJson(newValue) : null)
                    .status("SUCCESS")
                    .build();

            if (request != null) {
                auditLog.setIpAddress(getClientIpAddress(request));
                auditLog.setUserAgent(request.getHeader("User-Agent"));
                auditLog.setRequestMethod(request.getMethod());
                auditLog.setRequestUrl(request.getRequestURI());
            }

            auditLogRepository.save(auditLog);

        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    @Override
    public void logLogin(Long userId, String ipAddress, String userAgent, boolean success) {
        User user = userRepository.findById(userId).orElse(null);

        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(user != null ? user.getUsername() : null)
                .userEmail(user != null ? user.getEmail() : null)
                .action("LOGIN")
                .actionCategory("AUTHENTICATION")
                .description(success ? "User logged in successfully" : "Login failed")
                .status(success ? "SUCCESS" : "FAILURE")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(auditLog);

        log.info("Login {} for user: {}", success ? "success" : "failed",
                user != null ? user.getUsername() : "unknown");
    }

    @Override
    public void logLogout(Long userId) {
        User user = userRepository.findById(userId).orElse(null);

        AuditLog auditLog = AuditLog.builder()
                .userId(userId)
                .username(user != null ? user.getUsername() : null)
                .action("LOGOUT")
                .actionCategory("AUTHENTICATION")
                .description("User logged out")
                .status("SUCCESS")
                .build();

        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            auditLog.setIpAddress(getClientIpAddress(request));
            auditLog.setUserAgent(request.getHeader("User-Agent"));
        }

        auditLogRepository.save(auditLog);

        log.info("User logged out: {}", user != null ? user.getUsername() : "unknown");
    }

    @Override
    public void logCreate(String entityType, Long entityId, Object entity, Long userId) {
        try {
            Map<String, Object> entityMap = objectMapper.convertValue(entity, Map.class);

            log(
                    "CREATE",
                    getActionCategory(entityType),
                    String.format("Created %s", entityType),
                    userId,
                    entityType,
                    entityId,
                    null,
                    entityMap);
        } catch (Exception e) {
            log.error("Failed to log create action", e);
        }
    }

    @Override
    public void logUpdate(String entityType, Long entityId, Object oldEntity, Object newEntity, Long userId) {
        try {
            Map<String, Object> oldMap = objectMapper.convertValue(oldEntity, Map.class);
            Map<String, Object> newMap = objectMapper.convertValue(newEntity, Map.class);

            log(
                    "UPDATE",
                    getActionCategory(entityType),
                    String.format("Updated %s", entityType),
                    userId,
                    entityType,
                    entityId,
                    oldMap,
                    newMap);
        } catch (Exception e) {
            log.error("Failed to log update action", e);
        }
    }

    @Override
    public void logDelete(String entityType, Long entityId, Object entity, Long userId) {
        try {
            Map<String, Object> entityMap = objectMapper.convertValue(entity, Map.class);

            log(
                    "DELETE",
                    getActionCategory(entityType),
                    String.format("Deleted %s", entityType),
                    userId,
                    entityType,
                    entityId,
                    entityMap,
                    null);
        } catch (Exception e) {
            log.error("Failed to log delete action", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogDTO getAuditLogById(Long id) {
        AuditLog auditLog = auditLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found"));

        return auditMapper.toDTO(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDTO> getUserAuditLogs(Long userId, Pageable pageable) {
        Page<AuditLog> auditLogs = auditLogRepository.findByUserId(userId, pageable);
        return auditLogs.map(auditMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDTO> getEntityAuditLogs(String entityType, Long entityId, Pageable pageable) {
        List<AuditLog> auditLogs = auditLogRepository.findByEntity(entityType, entityId);

        // Convert to page manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), auditLogs.size());

        List<AuditLogDTO> dtos = auditLogs.subList(start, end).stream()
                .map(auditMapper::toDTO)
                .toList();

        return new org.springframework.data.domain.PageImpl<>(
                dtos, pageable, auditLogs.size());
    }

    @Override
    @Transactional(readOnly = true)
    public AuditStatisticsDTO getAuditStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> actionCounts = auditLogRepository.countActionsByCategory(startDate, endDate);
        List<Object[]> userActivity = auditLogRepository.getUserActivityStatistics(startDate, endDate);
        Double avgResponseTime = auditLogRepository.getAverageResponseTime(startDate, endDate);

        return AuditStatisticsDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .actionCounts(actionCounts)
                .userActivity(userActivity)
                .averageResponseTime(avgResponseTime)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogDTO> searchAuditLogs(
            String searchTerm, String action, String status,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        Page<AuditLog> auditLogs = auditLogRepository.searchAuditLogs(
                searchTerm, action, status, startDate, endDate, pageable);

        return auditLogs.map(auditMapper::toDTO);
    }

    @Override
    public void flagLog(Long logId, String reason) {
        AuditLog auditLog = auditLogRepository.findById(logId)
                .orElseThrow(() -> new ResourceNotFoundException("Audit log not found"));

        auditLog.flag(reason);
        auditLogRepository.save(auditLog);

        log.info("Flagged audit log: {} - Reason: {}", logId, reason);
    }

    // Helper methods

    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    private String getActionCategory(String entityType) {
        return switch (entityType.toUpperCase()) {
            case "USER" -> "USER_MANAGEMENT";
            case "BOOKING" -> "BOOKING";
            case "ORDER" -> "ORDER";
            case "COURT" -> "COURT_MANAGEMENT";
            case "PRODUCT" -> "PRODUCT_MANAGEMENT";
            case "TOURNAMENT" -> "TOURNAMENT";
            case "PAYMENT" -> "PAYMENT";
            default -> "GENERAL";
        };
    }
}
