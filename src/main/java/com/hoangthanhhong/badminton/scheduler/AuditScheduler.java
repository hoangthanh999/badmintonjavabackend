package com.hoangthanhhong.badminton.scheduler;

import com.hoangthanhhong.badminton.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditScheduler {

    private final AuditLogRepository auditLogRepository;

    /**
     * Clean up old audit logs every Sunday at 2:00 AM
     * Keep logs for 90 days (configurable)
     */
    @Scheduled(cron = "0 0 2 * * SUN")
    @Transactional
    public void cleanupOldAuditLogs() {
        log.info("Cleaning up old audit logs...");

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(90);

        try {
            // Archive or delete old logs
            // For now, we'll keep all logs, but you can implement deletion here

            log.info("Audit log cleanup completed");

        } catch (Exception e) {
            log.error("Failed to cleanup old audit logs", e);
        }
    }

    /**
     * Check for suspicious activities every hour
     */
    @Scheduled(cron = "0 0 * * * *")
    public void checkSuspiciousActivities() {
        log.info("Checking for suspicious activities...");

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        // Check for multiple failed login attempts
        List<com.hoangthanhhong.badminton.entity.AuditLog> failedLogins = auditLogRepository
                .findRecentFailedLogins(oneHourAgo);

        // Group by IP address
        java.util.Map<String, Long> failedLoginsByIp = failedLogins.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        com.hoangthanhhong.badminton.entity.AuditLog::getIpAddress,
                        java.util.stream.Collectors.counting()));

        // Flag IPs with more than 5 failed attempts
        for (java.util.Map.Entry<String, Long> entry : failedLoginsByIp.entrySet()) {
            if (entry.getValue() >= 5) {
                log.warn("Suspicious activity detected from IP: {} - {} failed login attempts",
                        entry.getKey(), entry.getValue());

                // TODO: Implement IP blocking or alert administrators
            }
        }

        log.info("Suspicious activity check completed");
    }
}
