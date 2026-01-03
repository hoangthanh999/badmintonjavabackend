package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.audit.AuditLogDTO;
import com.hoangthanhhong.badminton.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditMapper {

    public AuditLogDTO toDTO(AuditLog auditLog) {
        if (auditLog == null)
            return null;

        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .username(auditLog.getUsername())
                .userEmail(auditLog.getUserEmail())
                .userRole(auditLog.getUserRole())
                .action(auditLog.getAction())
                .actionCategory(auditLog.getActionCategory())
                .description(auditLog.getDescription())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .entityName(auditLog.getEntityName())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .changes(auditLog.getChanges())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .requestMethod(auditLog.getRequestMethod())
                .requestUrl(auditLog.getRequestUrl())
                .country(auditLog.getCountry())
                .city(auditLog.getCity())
                .deviceType(auditLog.getDeviceType())
                .operatingSystem(auditLog.getOperatingSystem())
                .browser(auditLog.getBrowser())
                .status(auditLog.getStatus())
                .errorMessage(auditLog.getErrorMessage())
                .timestamp(auditLog.getTimestamp())
                .responseTime(auditLog.getResponseTime())
                .isSensitive(auditLog.getIsSensitive())
                .isFlagged(auditLog.getIsFlagged())
                .flagReason(auditLog.getFlagReason())
                .build();
    }
}
