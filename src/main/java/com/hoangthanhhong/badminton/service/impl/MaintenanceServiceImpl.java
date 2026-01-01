package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceDTO;
import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.maintenance.CreateMaintenanceRequest;
import com.hoangthanhhong.badminton.dto.request.maintenance.UpdateMaintenanceRequest;
import com.hoangthanhhong.badminton.entity.*;
import com.hoangthanhhong.badminton.enums.MaintenanceStatus;
import com.hoangthanhhong.badminton.enums.MaintenanceType;
import com.hoangthanhhong.badminton.exception.BadRequestException;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.MaintenanceMapper;
import com.hoangthanhhong.badminton.repository.*;
import com.hoangthanhhong.badminton.service.MaintenanceService;
import com.hoangthanhhong.badminton.service.NotificationService;
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
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceChecklistItemRepository checklistItemRepository;
    private final MaintenanceLogRepository maintenanceLogRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final MaintenanceMapper maintenanceMapper;
    private final NotificationService notificationService;

    @Override
    public MaintenanceDTO createMaintenance(CreateMaintenanceRequest request) {
        // Validate court exists
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Court not found"));

        // Check for maintenance conflicts
        if (maintenanceRepository.hasMaintenanceConflict(
                request.getCourtId(),
                request.getScheduledDate(),
                request.getScheduledStartTime(),
                request.getScheduledEndTime())) {
            throw new BadRequestException("Maintenance schedule conflicts with existing maintenance");
        }

        // Create maintenance
        Maintenance maintenance = Maintenance.builder()
                .court(court)
                .type(request.getType())
                .title(request.getTitle())
                .description(request.getDescription())
                .status(MaintenanceStatus.SCHEDULED)
                .scheduledDate(request.getScheduledDate())
                .scheduledStartTime(request.getScheduledStartTime())
                .scheduledEndTime(request.getScheduledEndTime())
                .estimatedDuration(request.getEstimatedDuration())
                .estimatedCost(request.getEstimatedCost())
                .currency(request.getCurrency() != null ? request.getCurrency() : "VND")
                .assignedToId(request.getAssignedToId())
                .assignedToName(request.getAssignedToName())
                .technicianName(request.getTechnicianName())
                .technicianPhone(request.getTechnicianPhone())
                .vendorName(request.getVendorName())
                .vendorContact(request.getVendorContact())
                .priority(request.getPriority() != null ? request.getPriority() : "NORMAL")
                .severity(request.getSeverity())
                .isEmergency(request.getIsEmergency() != null ? request.getIsEmergency() : false)
                .isRecurring(request.getIsRecurring() != null ? request.getIsRecurring() : false)
                .recurrencePattern(request.getRecurrencePattern())
                .nextMaintenanceDate(request.getNextMaintenanceDate())
                .notes(request.getNotes())
                .requiresApproval(request.getRequiresApproval() != null ? request.getRequiresApproval() : false)
                .build();

        maintenance = maintenanceRepository.save(maintenance);

        // Create log
        createMaintenanceLog(maintenance, "CREATED", "Maintenance created", null, null);

        // Add checklist items if provided
        if (request.getChecklistItems() != null && !request.getChecklistItems().isEmpty()) {
            int sortOrder = 1;
            for (String task : request.getChecklistItems()) {
                MaintenanceChecklistItem item = MaintenanceChecklistItem.builder()
                        .maintenance(maintenance)
                        .task(task)
                        .sortOrder(sortOrder++)
                        .build();
                checklistItemRepository.save(item);
            }
        }

        // Send notification to assigned user
        if (maintenance.getAssignedToId() != null) {
            notificationService.sendNotification(
                    maintenance.getAssignedToId(),
                    com.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                    "Maintenance Assigned",
                    String.format("You have been assigned to maintenance: %s", maintenance.getTitle()),
                    java.util.Map.of(
                            "maintenanceId", maintenance.getId(),
                            "courtName", court.getName(),
                            "scheduledDate", maintenance.getScheduledDate().toString()));
        }

        log.info("Created maintenance: {} for court: {}", maintenance.getTitle(), court.getName());
        return maintenanceMapper.toDTO(maintenance);
    }

    @Override
    public MaintenanceDTO updateMaintenance(Long id, UpdateMaintenanceRequest request) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        // Store old values for logging
        String oldStatus = maintenance.getStatus().toString();
        LocalDate oldDate = maintenance.getScheduledDate();

        // Update fields
        if (request.getTitle() != null) {
            maintenance.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            maintenance.setDescription(request.getDescription());
        }
        if (request.getScheduledDate() != null) {
            maintenance.setScheduledDate(request.getScheduledDate());
        }
        if (request.getScheduledStartTime() != null) {
            maintenance.setScheduledStartTime(request.getScheduledStartTime());
        }
        if (request.getScheduledEndTime() != null) {
            maintenance.setScheduledEndTime(request.getScheduledEndTime());
        }
        if (request.getEstimatedDuration() != null) {
            maintenance.setEstimatedDuration(request.getEstimatedDuration());
        }
        if (request.getEstimatedCost() != null) {
            maintenance.setEstimatedCost(request.getEstimatedCost());
        }
        if (request.getAssignedToId() != null) {
            maintenance.setAssignedToId(request.getAssignedToId());
            maintenance.setAssignedToName(request.getAssignedToName());
        }
        if (request.getTechnicianName() != null) {
            maintenance.setTechnicianName(request.getTechnicianName());
        }
        if (request.getTechnicianPhone() != null) {
            maintenance.setTechnicianPhone(request.getTechnicianPhone());
        }
        if (request.getPriority() != null) {
            maintenance.setPriority(request.getPriority());
        }
        if (request.getNotes() != null) {
            maintenance.setNotes(request.getNotes());
        }

        maintenance = maintenanceRepository.save(maintenance);

        // Create log
        createMaintenanceLog(
                maintenance,
                "UPDATED",
                "Maintenance updated",
                String.format("Status: %s, Date: %s", oldStatus, oldDate),
                String.format("Status: %s, Date: %s", maintenance.getStatus(), maintenance.getScheduledDate()));

        log.info("Updated maintenance: {}", maintenance.getTitle());
        return maintenanceMapper.toDTO(maintenance);
    }

    @Override
    public void deleteMaintenance(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        if (maintenance.getStatus() == MaintenanceStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot delete maintenance in progress");
        }

        maintenance.softDelete();
        maintenanceRepository.save(maintenance);

        createMaintenanceLog(maintenance, "DELETED", "Maintenance deleted", null, null);

        log.info("Deleted maintenance: {}", maintenance.getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceDTO getMaintenanceById(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        return maintenanceMapper.toDTOWithDetails(maintenance);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> getAllMaintenance(Pageable pageable) {
        Page<Maintenance> maintenances = maintenanceRepository.findAll(pageable);
        return maintenances.map(maintenanceMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getCourtMaintenance(Long courtId) {
        List<Maintenance> maintenances = maintenanceRepository.findByCourtId(courtId);
        return maintenances.stream()
                .map(maintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getScheduledMaintenance(LocalDate startDate, LocalDate endDate) {
        List<Maintenance> maintenances = maintenanceRepository.findScheduledMaintenanceInDateRange(
                startDate, endDate, null);
        return maintenances.stream()
                .map(maintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getOverdueMaintenance() {
        List<Maintenance> maintenances = maintenanceRepository.findOverdueMaintenance(LocalDate.now());
        return maintenances.stream()
                .map(maintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getEmergencyMaintenance() {
        List<Maintenance> maintenances = maintenanceRepository.findEmergencyMaintenance();
        return maintenances.stream()
                .map(maintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void startMaintenance(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        if (maintenance.getStatus() != MaintenanceStatus.SCHEDULED) {
            throw new BadRequestException("Can only start scheduled maintenance");
        }

        maintenance.start();
        maintenanceRepository.save(maintenance);

        createMaintenanceLog(maintenance, "STARTED", "Maintenance started", null, null);

        // Send notification
        if (maintenance.getAssignedToId() != null) {
            notificationService.sendNotification(
                    maintenance.getAssignedToId(),
                    com.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                    "Maintenance Started",
                    String.format("Maintenance has started: %s", maintenance.getTitle()),
                    java.util.Map.of("maintenanceId", maintenance.getId()));
        }

        log.info("Started maintenance: {}", maintenance.getTitle());
    }

    @Override
    public void completeMaintenance(Long id, String completionNotes, Double actualCost) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        if (maintenance.getStatus() != MaintenanceStatus.IN_PROGRESS) {
            throw new BadRequestException("Can only complete maintenance in progress");
        }

        maintenance.setActualCost(actualCost);
        maintenance.complete(completionNotes);
        maintenanceRepository.save(maintenance);

        createMaintenanceLog(
                maintenance,
                "COMPLETED",
                "Maintenance completed",
                null,
                String.format("Actual cost: %.2f, Duration: %d minutes", actualCost, maintenance.getActualDuration()));

        // Send notification
        if (maintenance.getAssignedToId() != null) {
            notificationService.sendNotification(
                    maintenance.getAssignedToId(),
                    com.badminton.enums.NotificationType.SYSTEM_ANNOUNCEMENT,
                    "Maintenance Completed",
                    String.format("Maintenance has been completed: %s", maintenance.getTitle()),
                    java.util.Map.of("maintenanceId", maintenance.getId()));
        }

        log.info("Completed maintenance: {}", maintenance.getTitle());
    }

    @Override
    public void cancelMaintenance(Long id, Long cancelledBy, String reason) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        if (maintenance.getStatus() == MaintenanceStatus.COMPLETED) {
            throw new BadRequestException("Cannot cancel completed maintenance");
        }

        maintenance.cancel(cancelledBy, reason);
        maintenanceRepository.save(maintenance);

        createMaintenanceLog(
                maintenance,
                "CANCELLED",
                "Maintenance cancelled",
                null,
                String.format("Reason: %s", reason));

        log.info("Cancelled maintenance: {} - Reason: {}", maintenance.getTitle(), reason);
    }

    @Override
    public void postponeMaintenance(Long id, LocalDate newDate) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        if (maintenance.getStatus() != MaintenanceStatus.SCHEDULED) {
            throw new BadRequestException("Can only postpone scheduled maintenance");
        }

        LocalDate oldDate = maintenance.getScheduledDate();
        maintenance.postpone(newDate);
        maintenanceRepository.save(maintenance);

        createMaintenanceLog(
                maintenance,
                "POSTPONED",
                "Maintenance postponed",
                String.format("Old date: %s", oldDate),
                String.format("New date: %s", newDate));

        log.info("Postponed maintenance: {} from {} to {}", maintenance.getTitle(), oldDate, newDate);
    }

    @Override
    public void approveMaintenance(Long id, Long approvedBy, String notes) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        if (!maintenance.getRequiresApproval()) {
            throw new BadRequestException("This maintenance does not require approval");
        }

        if (maintenance.getApprovedBy() != null) {
            throw new BadRequestException("Maintenance already approved");
        }

        maintenance.approve(approvedBy, notes);
        maintenanceRepository.save(maintenance);

        createMaintenanceLog(
                maintenance,
                "APPROVED",
                "Maintenance approved",
                null,
                String.format("Approved by user ID: %d, Notes: %s", approvedBy, notes));

        log.info("Approved maintenance: {}", maintenance.getTitle());
    }

    @Override
    public void addChecklistItem(Long maintenanceId, String task, String description) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        // Get current max sort order
        List<MaintenanceChecklistItem> items = checklistItemRepository.findByMaintenanceId(maintenanceId);
        int sortOrder = items.isEmpty() ? 1
                : items.stream()
                        .mapToInt(MaintenanceChecklistItem::getSortOrder)
                        .max()
                        .orElse(0) + 1;

        MaintenanceChecklistItem item = MaintenanceChecklistItem.builder()
                .maintenance(maintenance)
                .task(task)
                .description(description)
                .sortOrder(sortOrder)
                .build();

        checklistItemRepository.save(item);

        log.info("Added checklist item to maintenance: {}", maintenance.getTitle());
    }

    @Override
    public void completeChecklistItem(Long itemId, Long completedBy, String notes) {
        MaintenanceChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found"));

        User user = userRepository.findById(completedBy)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        item.complete(completedBy, user.getName(), notes);
        checklistItemRepository.save(item);

        log.info("Completed checklist item: {} for maintenance: {}",
                item.getTask(), item.getMaintenance().getTitle());
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceStatisticsDTO getMaintenanceStatistics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> stats = maintenanceRepository.getMaintenanceStatisticsByType(startDate, endDate);
        Double totalCost = maintenanceRepository.getTotalMaintenanceCost(startDate, endDate, null);
        List<Object[]> statusCounts = maintenanceRepository.countByStatus(null);

        return MaintenanceStatisticsDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalCost(totalCost)
                .typeStatistics(stats)
                .statusCounts(statusCounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceStatisticsDTO getCourtMaintenanceStatistics(
            Long courtId, LocalDate startDate, LocalDate endDate) {

        Double totalCost = maintenanceRepository.getTotalMaintenanceCost(startDate, endDate, courtId);
        List<Object[]> statusCounts = maintenanceRepository.countByStatus(courtId);

        return MaintenanceStatisticsDTO.builder()
                .courtId(courtId)
                .startDate(startDate)
                .endDate(endDate)
                .totalCost(totalCost)
                .statusCounts(statusCounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaintenanceDTO> searchMaintenance(
            String searchTerm, String status, String type, Pageable pageable) {

        MaintenanceStatus maintenanceStatus = status != null ? MaintenanceStatus.valueOf(status) : null;
        MaintenanceType maintenanceType = type != null ? MaintenanceType.valueOf(type) : null;

        Page<Maintenance> maintenances = maintenanceRepository.searchMaintenance(
                searchTerm, maintenanceStatus, maintenanceType, pageable);

        return maintenances.map(maintenanceMapper::toDTO);
    }

    // Helper method
    private void createMaintenanceLog(Maintenance maintenance, String action, String description,
            String oldValue, String newValue) {
        MaintenanceLog log = MaintenanceLog.builder()
                .maintenance(maintenance)
                .action(action)
                .description(description)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();

        maintenanceLogRepository.save(log);
    }
}
