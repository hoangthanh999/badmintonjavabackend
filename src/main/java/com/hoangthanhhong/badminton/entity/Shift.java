package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shifts", indexes = {
        @Index(name = "idx_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shift extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "break_duration")
    private Integer breakDuration; // in minutes

    @Column(name = "work_hours")
    private Double workHours;

    @Column(length = 50)
    private String color; // For UI display

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "shift")
    @Builder.Default
    private List<Attendance> attendances = new ArrayList<>();

    // === HELPER METHODS ===

    public Double calculateWorkHours() {
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        if (breakDuration != null) {
            minutes -= breakDuration;
        }
        return minutes / 60.0;
    }

    @PrePersist
    @PreUpdate
    public void calculateHours() {
        this.workHours = calculateWorkHours();
    }
}
