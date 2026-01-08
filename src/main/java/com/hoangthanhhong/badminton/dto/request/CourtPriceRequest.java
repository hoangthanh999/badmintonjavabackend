package com.hoangthanhhong.badminton.dto.request;

import com.hoangthanhhong.badminton.enums.DayType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtPriceRequest {

    @NotNull(message = "Loại ngày không được để trống")
    private DayType dayType;

    @NotNull(message = "Giờ bắt đầu không được để trống")
    private LocalTime timeStart;

    @NotNull(message = "Giờ kết thúc không được để trống")
    private LocalTime timeEnd;

    @NotNull(message = "Giá không được để trống")
    @Positive(message = "Giá phải lớn hơn 0")
    private Double price;

    private Boolean isPeakHour;

    private Boolean isActive;
}
