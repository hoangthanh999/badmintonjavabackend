package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.DayType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Entity
@Table(name = "court_prices", indexes = {
        @Index(name = "idx_court_time", columnList = "court_id, time_start, time_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtPrice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_type", nullable = false, length = 20)
    private DayType dayType; // WEEKDAY, WEEKEND, HOLIDAY

    @Column(name = "time_start", nullable = false)
    private LocalTime timeStart;

    @Column(name = "time_end", nullable = false)
    private LocalTime timeEnd;

    @Column(nullable = false)
    private Double price;

    @Column(name = "is_peak_hour")
    @Builder.Default
    private Boolean isPeakHour = false;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
