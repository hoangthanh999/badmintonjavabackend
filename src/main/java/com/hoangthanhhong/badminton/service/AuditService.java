package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.audit.AuditLogDTO;
import com.hoangthanhhong.badminton.dto.audit.AuditStatisticsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

public interface AuditService {

    // Create audit log
    void log(String action, String actionCategory, String description,
            Long userId, String entityType, Long entityId,
            Map<String, Object> oldValue, Map<String, Object> newValue);

    void logLogin(Long userId, String ipAddress, String userAgent, boolean success);

    void logLogout(Long userId);

    void logCreate(String entityType, Long entityId, Object entity, Long userId);

    void logUpdate(String entityType, Long entityId, Object oldEntity, Object newEntity, Long userId);

    void logDelete(String entityType, Long entityId, Object entity, Long userId);

    // Get audit logs
    AuditLogDTO getAuditLogById(Long id);

    Page<AuditLogDTO> getUserAuditLogs(Long userId, Pageable pageable);

    Page<AuditLogDTO> getEntityAuditLogs(String entityType, Long entityId, Pageable pageable);

    // Statistics
    AuditStatisticsDTO getAuditStatistics(LocalDateTime startDate, LocalDateTime endDate);

    // Search
    Page<AuditLogDTO> searchAuditLogs(String searchTerm, String action, String status,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    // Flag management
    void flagLog(Long logId, String reason);
}
