package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceDTO;
import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.maintenance.CreateMaintenanceRequest;
import com.hoangthanhhong.badminton.dto.request.maintenance.UpdateMaintenanceRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface MaintenanceService {

    // CRUD
    MaintenanceDTO createMaintenance(CreateMaintenanceRequest request);

    MaintenanceDTO updateMaintenance(Long id, UpdateMaintenanceRequest request);

    void deleteMaintenance(Long id);

    MaintenanceDTO getMaintenanceById(Long id);

    Page<MaintenanceDTO> getAllMaintenance(Pageable pageable);

    // Get maintenance
    List<MaintenanceDTO> getCourtMaintenance(Long courtId);

    List<MaintenanceDTO> getScheduledMaintenance(LocalDate startDate, LocalDate endDate);

    List<MaintenanceDTO> getOverdueMaintenance();

    List<MaintenanceDTO> getEmergencyMaintenance();

    // Actions
    void startMaintenance(Long id);

    void completeMaintenance(Long id, String completionNotes, Double actualCost);

    void cancelMaintenance(Long id, Long cancelledBy, String reason);

    void postponeMaintenance(Long id, LocalDate newDate);

    void approveMaintenance(Long id, Long approvedBy, String notes);

    // Checklist
    void addChecklistItem(Long maintenanceId, String task, String description);

    void completeChecklistItem(Long itemId, Long completedBy, String notes);

    // Statistics
    MaintenanceStatisticsDTO getMaintenanceStatistics(LocalDate startDate, LocalDate endDate);

    MaintenanceStatisticsDTO getCourtMaintenanceStatistics(Long courtId, LocalDate startDate, LocalDate endDate);

    // Search
    Page<MaintenanceDTO> searchMaintenance(String searchTerm, String status, String type, Pageable pageable);
}
