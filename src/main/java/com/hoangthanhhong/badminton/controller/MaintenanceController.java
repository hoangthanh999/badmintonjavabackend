package com.hoangthanhhong.badminton.controller;

import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceDTO;
import com.hoangthanhhong.badminton.dto.maintenance.MaintenanceStatisticsDTO;
import com.hoangthanhhong.badminton.dto.request.maintenance.CreateMaintenanceRequest;
import com.hoangthanhhong.badminton.dto.request.maintenance.UpdateMaintenanceRequest;
import com.hoangthanhhong.badminton.dto.response.ApiResponse;
import com.hoangthanhhong.badminton.service.MaintenanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/maintenance")
@RequiredArgsConstructor
@Tag(name = "Maintenance", description = "Maintenance Management APIs")
@SecurityRequirement(name = "bearerAuth")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create maintenance", description = "Create a new maintenance schedule")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> createMaintenance(
            @Valid @RequestBody CreateMaintenanceRequest request) {

        MaintenanceDTO maintenance = maintenanceService.createMaintenance(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(maintenance, "Maintenance created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update maintenance", description = "Update maintenance details")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> updateMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMaintenanceRequest request) {

        MaintenanceDTO maintenance = maintenanceService.updateMaintenance(id, request);

        return ResponseEntity.ok(
                ApiResponse.success(maintenance, "Maintenance updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete maintenance", description = "Delete a maintenance record")
    public ResponseEntity<ApiResponse<Void>> deleteMaintenance(@PathVariable Long id) {
        maintenanceService.deleteMaintenance(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Maintenance deleted successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get maintenance by ID", description = "Get maintenance details by ID")
    public ResponseEntity<ApiResponse<MaintenanceDTO>> getMaintenanceById(@PathVariable Long id) {
        MaintenanceDTO maintenance = maintenanceService.getMaintenanceById(id);

        return ResponseEntity.ok(ApiResponse.success(maintenance));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get all maintenance", description = "Get all maintenance records with pagination")
    public ResponseEntity<ApiResponse<Page<MaintenanceDTO>>> getAllMaintenance(Pageable pageable) {
        Page<MaintenanceDTO> maintenances = maintenanceService.getAllMaintenance(pageable);

        return ResponseEntity.ok(ApiResponse.success(maintenances));
    }

    @GetMapping("/court/{courtId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get court maintenance", description = "Get all maintenance for a specific court")
    public ResponseEntity<ApiResponse<List<MaintenanceDTO>>> getCourtMaintenance(
            @PathVariable Long courtId) {

        List<MaintenanceDTO> maintenances = maintenanceService.getCourtMaintenance(courtId);

        return ResponseEntity.ok(ApiResponse.success(maintenances));
    }

    @GetMapping("/scheduled")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get scheduled maintenance", description = "Get scheduled maintenance in date range")
    public ResponseEntity<ApiResponse<List<MaintenanceDTO>>> getScheduledMaintenance(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<MaintenanceDTO> maintenances = maintenanceService.getScheduledMaintenance(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(maintenances));
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get overdue maintenance", description = "Get all overdue maintenance")
    public ResponseEntity<ApiResponse<List<MaintenanceDTO>>> getOverdueMaintenance() {
        List<MaintenanceDTO> maintenances = maintenanceService.getOverdueMaintenance();

        return ResponseEntity.ok(ApiResponse.success(maintenances));
    }

    @GetMapping("/emergency")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get emergency maintenance", description = "Get all emergency maintenance")
    public ResponseEntity<ApiResponse<List<MaintenanceDTO>>> getEmergencyMaintenance() {
        List<MaintenanceDTO> maintenances = maintenanceService.getEmergencyMaintenance();

        return ResponseEntity.ok(ApiResponse.success(maintenances));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Start maintenance", description = "Start a scheduled maintenance")
    public ResponseEntity<ApiResponse<Void>> startMaintenance(@PathVariable Long id) {
        maintenanceService.startMaintenance(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Maintenance started successfully"));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Complete maintenance", description = "Complete a maintenance")
    public ResponseEntity<ApiResponse<Void>> completeMaintenance(
            @PathVariable Long id,
            @RequestParam String completionNotes,
            @RequestParam Double actualCost) {

        maintenanceService.completeMaintenance(id, completionNotes, actualCost);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Maintenance completed successfully"));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cancel maintenance", description = "Cancel a maintenance")
    public ResponseEntity<ApiResponse<Void>> cancelMaintenance(
            @PathVariable Long id,
            @RequestParam Long cancelledBy,
            @RequestParam String reason) {

        maintenanceService.cancelMaintenance(id, cancelledBy, reason);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Maintenance cancelled successfully"));
    }

    @PostMapping("/{id}/postpone")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Postpone maintenance", description = "Postpone a maintenance to a new date")
    public ResponseEntity<ApiResponse<Void>> postponeMaintenance(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate newDate) {

        maintenanceService.postponeMaintenance(id, newDate);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Maintenance postponed successfully"));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve maintenance", description = "Approve a maintenance request")
    public ResponseEntity<ApiResponse<Void>> approveMaintenance(
            @PathVariable Long id,
            @RequestParam Long approvedBy,
            @RequestParam(required = false) String notes) {

        maintenanceService.approveMaintenance(id, approvedBy, notes);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Maintenance approved successfully"));
    }

    @PostMapping("/{id}/checklist")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Add checklist item", description = "Add a checklist item to maintenance")
    public ResponseEntity<ApiResponse<Void>> addChecklistItem(
            @PathVariable Long id,
            @RequestParam String task,
            @RequestParam(required = false) String description) {

        maintenanceService.addChecklistItem(id, task, description);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Checklist item added successfully"));
    }

    @PostMapping("/checklist/{itemId}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Complete checklist item", description = "Mark a checklist item as completed")
    public ResponseEntity<ApiResponse<Void>> completeChecklistItem(
            @PathVariable Long itemId,
            @RequestParam Long completedBy,
            @RequestParam(required = false) String notes) {

        maintenanceService.completeChecklistItem(itemId, completedBy, notes);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Checklist item completed successfully"));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get maintenance statistics", description = "Get maintenance statistics for a date range")
    public ResponseEntity<ApiResponse<MaintenanceStatisticsDTO>> getMaintenanceStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        MaintenanceStatisticsDTO statistics = maintenanceService.getMaintenanceStatistics(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/statistics/court/{courtId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get court maintenance statistics", description = "Get maintenance statistics for a specific court")
    public ResponseEntity<ApiResponse<MaintenanceStatisticsDTO>> getCourtMaintenanceStatistics(
            @PathVariable Long courtId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        MaintenanceStatisticsDTO statistics = maintenanceService.getCourtMaintenanceStatistics(
                courtId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Search maintenance", description = "Search maintenance records")
    public ResponseEntity<ApiResponse<Page<MaintenanceDTO>>> searchMaintenance(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            Pageable pageable) {

        Page<MaintenanceDTO> maintenances = maintenanceService.searchMaintenance(
                searchTerm, status, type, pageable);

        return ResponseEntity.ok(ApiResponse.success(maintenances));
    }
}
