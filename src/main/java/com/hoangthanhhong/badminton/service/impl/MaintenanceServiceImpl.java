package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceDTO;
import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.maintenance.CreateMaintenanceRequest;
import com.hoangthanhhong.badminton.dto.request.maintenance.UpdateMaintenanceRequest;
import com.hoangthanhhong.badminton.entity.*;
import com.hoangthanhhong.badminton.enums.CourtStatus;
import com.hoangthanhhong.badminton.enums.MaintenanceStatus;
import com.hoangthanhhong.badminton.enums.MaintenanceType;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.MaintenanceMapper;
import com.hoangthanhhong.badminton.repository.CourtRepository;
import com.hoangthanhhong.badminton.repository.MaintenanceRepository;
import com.hoangthanhhong.badminton.repository.MaintenanceChecklistItemRepository;
import com.hoangthanhhong.badminton.service.MaintenanceService;
import com.hoangthanhhong.badminton.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaintenanceServiceImpl implements MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final CourtRepository courtRepository;
    private final MaintenanceChecklistItemRepository checklistItemRepository;
    private final MaintenanceMapper maintenanceMapper;
    private final NotificationService notificationService;

    @Override
    public MaintenanceDTO createMaintenance(CreateMaintenanceRequest request) {
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new ResourceNotFoundException("Court not found"));

        Maintenance maintenance = Maintenance.builder()
                .court(court)
                .type(request.getType())
                .title(request.getTitle())
                .description(request.getDescription())
                .scheduledDate(request.getScheduledDate())
                .scheduledStartTime(request.getScheduledStartTime())
                .scheduledEndTime(request.getScheduledEndTime())
                .estimatedDuration(request.getEstimatedDuration())
                .estimatedCost(request.getEstimatedCost())
                .currency(request.getCurrency())
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
                .status(MaintenanceStatus.SCHEDULED)
                .build();

        maintenance = maintenanceRepository.save(maintenance);

        // Add checklist items
        if (request.getChecklistItems() != null && !request.getChecklistItems().isEmpty()) {
            int order = 0;
            for (String task : request.getChecklistItems()) {
                MaintenanceChecklistItem item = MaintenanceChecklistItem.builder()
                        .maintenance(maintenance)
                        .task(task)
                        .sortOrder(order++)
                        .isCompleted(false)
                        .isMandatory(true)
                        .build();
                checklistItemRepository.save(item);
            }
        }

        log.info("Created maintenance: {}", maintenance.getId());
        return maintenanceMapper.toDTOWithDetails(maintenance);
    }

    @Override
    public MaintenanceDTO updateMaintenance(Long id, UpdateMaintenanceRequest request) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        if (request.getTitle() != null)
            maintenance.setTitle(request.getTitle());
        if (request.getDescription() != null)
            maintenance.setDescription(request.getDescription());
        if (request.getScheduledDate() != null)
            maintenance.setScheduledDate(request.getScheduledDate());
        if (request.getScheduledStartTime() != null)
            maintenance.setScheduledStartTime(request.getScheduledStartTime());
        if (request.getScheduledEndTime() != null)
            maintenance.setScheduledEndTime(request.getScheduledEndTime());
        if (request.getEstimatedDuration() != null)
            maintenance.setEstimatedDuration(request.getEstimatedDuration());
        if (request.getEstimatedCost() != null)
            maintenance.setEstimatedCost(request.getEstimatedCost());
        if (request.getAssignedToId() != null)
            maintenance.setAssignedToId(request.getAssignedToId());
        if (request.getAssignedToName() != null)
            maintenance.setAssignedToName(request.getAssignedToName());
        if (request.getTechnicianName() != null)
            maintenance.setTechnicianName(request.getTechnicianName());
        if (request.getTechnicianPhone() != null)
            maintenance.setTechnicianPhone(request.getTechnicianPhone());
        if (request.getPriority() != null)
            maintenance.setPriority(request.getPriority());
        if (request.getNotes() != null)
            maintenance.setNotes(request.getNotes());

        maintenance = maintenanceRepository.save(maintenance);
        log.info("Updated maintenance: {}", id);
        return maintenanceMapper.toDTO(maintenance);
    }

    @Override
    public void deleteMaintenance(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        maintenance.softDelete();
        maintenanceRepository.save(maintenance);
        log.info("Deleted maintenance: {}", id);
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
        return maintenanceRepository.findAll(pageable).map(maintenanceMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getCourtMaintenance(Long courtId) {
        return maintenanceRepository.findByCourtId(courtId).stream()
                .map(maintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getScheduledMaintenance(LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findScheduledMaintenanceInDateRange(startDate, endDate, null).stream()
                .map(maintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getOverdueMaintenance() {
        return maintenanceRepository.findOverdueMaintenance(LocalDate.now()).stream()
                .map(maintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getEmergencyMaintenance() {
        return maintenanceRepository.findEmergencyMaintenance().stream()
                .map(maintenanceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void startMaintenance(Long id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        maintenance.start();
        maintenance.getCourt().setStatus(CourtStatus.MAINTENANCE);
        maintenanceRepository.save(maintenance);
        log.info("Started maintenance: {}", id);
    }

    @Override
    public void completeMaintenance(Long id, String completionNotes, Double actualCost) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        maintenance.setActualCost(actualCost);
        maintenance.complete(completionNotes);
        maintenance.getCourt().setStatus(CourtStatus.AVAILABLE);
        maintenanceRepository.save(maintenance);
        log.info("Completed maintenance: {}", id);
    }

    @Override
    public void cancelMaintenance(Long id, Long cancelledBy, String reason) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        maintenance.cancel(cancelledBy, reason);
        if (maintenance.getCourt().getStatus() == CourtStatus.MAINTENANCE) {
            maintenance.getCourt().setStatus(CourtStatus.AVAILABLE);
        }
        maintenanceRepository.save(maintenance);
        log.info("Cancelled maintenance: {}", id);
    }

    @Override
    public void postponeMaintenance(Long id, LocalDate newDate) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        maintenance.postpone(newDate);
        maintenanceRepository.save(maintenance);
        log.info("Postponed maintenance: {} to {}", id, newDate);
    }

    @Override
    public void approveMaintenance(Long id, Long approvedBy, String notes) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));
        maintenance.approve(approvedBy, notes);
        maintenanceRepository.save(maintenance);
        log.info("Approved maintenance: {}", id);
    }

    @Override
    public void addChecklistItem(Long maintenanceId, String task, String description) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance not found"));

        MaintenanceChecklistItem item = MaintenanceChecklistItem.builder()
                .maintenance(maintenance)
                .task(task)
                .description(description)
                .isCompleted(false)
                .isMandatory(true)
                .build();

        checklistItemRepository.save(item);
        log.info("Added checklist item to maintenance: {}", maintenanceId);
    }

    @Override
    public void completeChecklistItem(Long itemId, Long completedBy, String notes) {
        MaintenanceChecklistItem item = checklistItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Checklist item not found"));
        item.complete(completedBy, null, notes);
        checklistItemRepository.save(item);
        log.info("Completed checklist item: {}", itemId);
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceStatisticsDTO getMaintenanceStatistics(LocalDate startDate, LocalDate endDate) {
        List<Object[]> stats = maintenanceRepository.getMaintenanceStatisticsByCourt(startDate, endDate);
        List<Object[]> typeStats = maintenanceRepository.getMaintenanceStatisticsByType(startDate, endDate);
        Double totalCost = maintenanceRepository.getTotalMaintenanceCost(startDate, endDate, null);
        List<Object[]> statusCounts = maintenanceRepository.countByStatus(null);

        return MaintenanceStatisticsDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalCost(totalCost)
                .typeStatistics(typeStats)
                .statusCounts(statusCounts)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public MaintenanceStatisticsDTO getCourtMaintenanceStatistics(Long courtId, LocalDate startDate,
            LocalDate endDate) {
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
    public Page<MaintenanceDTO> searchMaintenance(String searchTerm, String status, String type, Pageable pageable) {
        MaintenanceStatus maintenanceStatus = status != null ? MaintenanceStatus.valueOf(status) : null;
        MaintenanceType maintenanceType = type != null ? MaintenanceType.valueOf(type) : null;

        return maintenanceRepository.searchMaintenance(searchTerm, maintenanceStatus, maintenanceType, pageable)
                .map(maintenanceMapper::toDTO);
    }
}
