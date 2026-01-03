package com.hoangthanhhong.badminton.dto.request.attendance;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInRequest {

    private Long shiftId;

    @NotBlank(message = "Location is required")
    private String location;

    private String ipAddress;
    private String device;
    private String notes;
}
