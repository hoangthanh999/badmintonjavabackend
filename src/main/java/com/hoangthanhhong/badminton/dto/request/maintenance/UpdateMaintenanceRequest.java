package com.hoangthanhhong.badminton.dto.request.maintenance;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMaintenanceRequest {

    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @FutureOrPresent(message = "Scheduled date must be today or in the future")
    private LocalDate scheduledDate;

    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;

    @Min(value = 1, message = "Estimated duration must be at least 1 minute")
    private Integer estimatedDuration;

    @DecimalMin(value = "0.0", message = "Estimated cost must be non-negative")
    private Double estimatedCost;

    private Long assignedToId;
    private String assignedToName;

    @Size(max = 100, message = "Technician name must not exceed 100 characters")
    private String technicianName;

    @Pattern(regexp = "^[0-9+\\-\\s()]*$", message = "Invalid phone number format")
    private String technicianPhone;

    @Pattern(regexp = "LOW|NORMAL|HIGH|URGENT", message = "Priority must be LOW, NORMAL, HIGH, or URGENT")
    private String priority;

    @Size(max = 2000, message = "Notes must not exceed 2000 characters")
    private String notes;
}
