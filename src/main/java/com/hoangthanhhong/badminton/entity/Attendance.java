package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.AttendanceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendances", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "date" }), indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_date", columnList = "date"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AttendanceStatus status = AttendanceStatus.ABSENT;

    // === CHECK IN/OUT ===

    @Column(name = "check_in_time")
    private LocalTime checkInTime;

    @Column(name = "check_out_time")
    private LocalTime checkOutTime;

    @Column(name = "check_in_location", length = 200)
    private String checkInLocation;

    @Column(name = "check_out_location", length = 200)
    private String checkOutLocation;

    @Column(name = "check_in_ip", length = 50)
    private String checkInIp;

    @Column(name = "check_out_ip", length = 50)
    private String checkOutIp;

    @Column(name = "check_in_device", length = 200)
    private String checkInDevice;

    @Column(name = "check_out_device", length = 200)
    private String checkOutDevice;

    // === SCHEDULE ===

    @Column(name = "scheduled_start_time")
    private LocalTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private LocalTime scheduledEndTime;

    // === DURATION ===

    @Column(name = "work_hours")
    private Double workHours; // in hours

    @Column(name = "overtime_hours")
    private Double overtimeHours;

    @Column(name = "break_hours")
    private Double breakHours;

    // === LATE/EARLY ===

    @Column(name = "is_late")
    @Builder.Default
    private Boolean isLate = false;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "is_early_departure")
    @Builder.Default
    private Boolean isEarlyDeparture = false;

    @Column(name = "early_departure_minutes")
    private Integer earlyDepartureMinutes;

    // === LEAVE ===

    @Column(name = "leave_type", length = 50)
    private String leaveType; // SICK, VACATION, PERSONAL, etc.

    @Column(name = "leave_reason", columnDefinition = "TEXT")
    private String leaveReason;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    // === NOTES ===

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    // === VERIFICATION ===

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    // === RELATIONSHIPS ===

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    // === HELPER METHODS ===

    public void checkIn(LocalTime time, String location, String ip, String device) {
        this.checkInTime = time;
        this.checkInLocation = location;
        this.checkInIp = ip;
        this.checkInDevice = device;
        this.status = AttendanceStatus.PRESENT;

        // Check if late
        if (scheduledStartTime != null && time.isAfter(scheduledStartTime)) {
            this.isLate = true;
            this.lateMinutes = (int) java.time.Duration.between(scheduledStartTime, time).toMinutes();
        }
    }

    public void checkOut(LocalTime time, String location, String ip, String device) {
        this.checkOutTime = time;
        this.checkOutLocation = location;
        this.checkOutIp = ip;
        this.checkOutDevice = device;

        // Calculate work hours
        if (checkInTime != null) {
            this.workHours = java.time.Duration.between(checkInTime, time).toMinutes() / 60.0;

            // Subtract break hours if any
            if (breakHours != null) {
                this.workHours -= breakHours;
            }

            // Calculate overtime
            if (scheduledEndTime != null) {
                double scheduledHours = java.time.Duration.between(scheduledStartTime, scheduledEndTime).toMinutes()
                        / 60.0;
                if (workHours > scheduledHours) {
                    this.overtimeHours = workHours - scheduledHours;
                }
            }
        }

        // Check if early departure
        if (scheduledEndTime != null && time.isBefore(scheduledEndTime)) {
            this.isEarlyDeparture = true;
            this.earlyDepartureMinutes = (int) java.time.Duration.between(time, scheduledEndTime).toMinutes();
        }
    }

    public void markAsAbsent() {
        this.status = AttendanceStatus.ABSENT;
    }

    public void markAsLeave(String leaveType, String reason) {
        this.status = AttendanceStatus.LEAVE;
        this.leaveType = leaveType;
        this.leaveReason = reason;
    }

    public void markAsHoliday() {
        this.status = AttendanceStatus.HOLIDAY;
    }

    public void approve(Long approvedBy) {
        this.isApproved = true;
        this.approvedBy = approvedBy;
        this.approvedAt = LocalDateTime.now();
    }

    public void verify(Long verifiedBy) {
        this.isVerified = true;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = LocalDateTime.now();
    }

    public boolean isPresent() {
        return status == AttendanceStatus.PRESENT;
    }

    public boolean isAbsent() {
        return status == AttendanceStatus.ABSENT;
    }

    public boolean isOnLeave() {
        return status == AttendanceStatus.LEAVE;
    }

    public Double getTotalHours() {
        return workHours != null ? workHours : 0.0;
    }
}
