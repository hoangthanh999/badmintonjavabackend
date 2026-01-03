package com.hoangthanhhong.badminton.dto.request.maintenance;

import com.hoangthanhhong.badminton.enums.MaintenanceType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMaintenanceRequest {

    @NotNull(message = "Court ID is required")
    private Long courtId;

    @NotNull(message = "Maintenance type is required")
    private MaintenanceType type;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Scheduled date is required")
    @FutureOrPresent(message = "Scheduled date must be today or in the future")
    private LocalDate scheduledDate;

    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;

    @Min(value = 1, message = "Estimated duration must be at least 1 minute")
    private Integer estimatedDuration;

    @DecimalMin(value = "0.0", message = "Estimated cost must be non-negative")
    private Double estimatedCost;

    private String currency;

    private Long assignedToId;
    private String assignedToName;

    @Size(max = 100, message = "Technician name must not exceed 100 characters")
    private String technicianName;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Invalid phone number format")
    private String technicianPhone;

    @Size(max = 200, message = "Vendor name must not exceed 200 characters")
    private String vendorName;

    @Size(max = 100, message = "Vendor contact must not exceed 100 characters")
    private String vendorContact;

    @Pattern(regexp = "LOW|NORMAL|HIGH|URGENT", message = "Priority must be LOW, NORMAL, HIGH, or URGENT")
    private String priority;

    @Pattern(regexp = "MINOR|MODERATE|MAJOR|CRITICAL", message = "Invalid severity level")
    private String severity;

    private Boolean isEmergency;
    private Boolean isRecurring;

    @Pattern(regexp = "DAILY|WEEKLY|MONTHLY|QUARTERLY|YEARLY", message = "Invalid recurrence pattern")
    private String recurrencePattern;

    private LocalDate nextMaintenanceDate;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;

    private Boolean requiresApproval;

    private List<String> checklistItems;
}
