
package com.hoangthanhhong.badminton.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hoangthanhhong.badminton.enums.DayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtPriceResponse {

    private Long id;

    private Long courtId;

    private String courtName;

    private DayType dayType;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime timeStart;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime timeEnd;

    private Double price;

    private Boolean isPeakHour;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}