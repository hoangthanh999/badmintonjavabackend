
package com.hoangthanhhong.badminton.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hoangthanhhong.badminton.enums.CourtStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourtResponse {

    private Long id;

    private String name;

    private String location;

    private String description;

    private CourtStatus status;

    private String imageUrl;

    private Double basePrice;

    private Double peakHourPrice;

    private Double weekendPrice;

    private Integer courtNumber;

    private String floorType;

    private String lightingQuality;

    private Boolean hasAirConditioning;

    private Integer maxPlayers;

    private Double areaSize;

    private Double rating;

    private Integer totalReviews;

    private List<String> amenityNames;

    private Integer totalBookings;

    private Boolean isAvailable;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
