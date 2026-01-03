package com.hoangthanhhong.badminton.scheduler;

import com.hoangthanhhong.badminton.entity.Maintenance;
import com.hoangthanhhong.badminton.repository.MaintenanceRepository;
import com.hoangthanhhong.badminton.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaintenanceScheduler {

    private final MaintenanceRepository maintenanceRepository;
    private final NotificationService notificationService;

    /**
     * Check for overdue maintenance every day at 8:00 AM
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void checkOverdueMaintenance() {
        log.info("Checking for overdue maintenance...");

        List<Maintenance> overdueMaintenance = maintenanceRepository.findOverdueMaintenance(
                LocalDate.now());

        for (Maintenance maintenance : overdueMaintenance) {
            if (maintenance.getAssignedToId() != null) {
                notificationService.sendNotification(
                        maintenance.getAssignedToId(),
                        com.hoangthanhhong.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                        "Overdue Maintenance",
                        String.format("Maintenance is overdue: %s for court %s",
                                maintenance.getTitle(),
                                maintenance.getCourt().getName()),
                        java.util.Map.of(
                                "maintenanceId", maintenance.getId(),
                                "scheduledDate", maintenance.getScheduledDate().toString()));
            }
        }

        log.info("Found {} overdue maintenance records", overdueMaintenance.size());
    }

    /**
     * Send reminders for upcoming maintenance every day at 7:00 AM
     */
    @Scheduled(cron = "0 0 7 * * *")
    public void sendMaintenanceReminders() {
        log.info("Sending maintenance reminders...");

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Maintenance> upcomingMaintenance = maintenanceRepository.findScheduledMaintenanceInDateRange(
                tomorrow, tomorrow, null);

        for (Maintenance maintenance : upcomingMaintenance) {
            if (maintenance.getAssignedToId() != null) {
                notificationService.sendNotification(
                        maintenance.getAssignedToId(),
                        com.hoangthanhhong.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                        "Maintenance Reminder",
                        String.format("Maintenance scheduled for tomorrow: %s for court %s",
                                maintenance.getTitle(),
                                maintenance.getCourt().getName()),
                        java.util.Map.of(
                                "maintenanceId", maintenance.getId(),
                                "scheduledDate", maintenance.getScheduledDate().toString()));
            }
        }

        log.info("Sent {} maintenance reminders", upcomingMaintenance.size());
    }

    /**
     * Create recurring maintenance every day at 1:00 AM
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void createRecurringMaintenance() {
        log.info("Creating recurring maintenance...");

        LocalDate today = LocalDate.now();
        List<Maintenance> recurringMaintenance = maintenanceRepository.findRecurringMaintenanceDue(today);

        int created = 0;
        for (Maintenance maintenance : recurringMaintenance) {
            try {
                // Create new maintenance based on recurring pattern
                Maintenance newMaintenance = Maintenance.builder()
                        .court(maintenance.getCourt())
                        .type(maintenance.getType())
                        .title(maintenance.getTitle())
                        .description(maintenance.getDescription())
                        .status(com.hoangthanhhong.badminton.enums.MaintenanceStatus.SCHEDULED)
                        .scheduledDate(maintenance.getNextMaintenanceDate())
                        .scheduledStartTime(maintenance.getScheduledStartTime())
                        .scheduledEndTime(maintenance.getScheduledEndTime())
                        .estimatedDuration(maintenance.getEstimatedDuration())
                        .estimatedCost(maintenance.getEstimatedCost())
                        .currency(maintenance.getCurrency())
                        .assignedToId(maintenance.getAssignedToId())
                        .assignedToName(maintenance.getAssignedToName())
                        .priority(maintenance.getPriority())
                        .isRecurring(true)
                        .recurrencePattern(maintenance.getRecurrencePattern())
                        .build();

                maintenanceRepository.save(newMaintenance);
                created++;

            } catch (Exception e) {
                log.error("Failed to create recurring maintenance for ID: {}", maintenance.getId(), e);
            }
        }

        log.info("Created {} recurring maintenance records", created);
    }
}
