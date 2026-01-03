package com.hoangthanhhong.badminton.dto.request.attendance;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequest {

    @NotNull(message = "Date is required")
    @Future(message = "Leave date must be in the future")
    private LocalDate date;

    @NotBlank(message = "Leave type is required")
    @Pattern(regexp = "SICK|VACATION|PERSONAL|EMERGENCY|UNPAID|OTHER", message = "Invalid leave type")
    private String leaveType;

    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    private String reason;

    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
}
