package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.BookingStatus;
import com.hoangthanhhong.badminton.enums.BookingType;
import com.hoangthanhhong.badminton.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_court_id", columnList = "court_id"),
        @Index(name = "idx_date", columnList = "date"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_booking_date_time", columnList = "court_id, date, time_start, time_end")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@NamedStoredProcedureQuery(name = "sp_create_recurring_booking", procedureName = "sp_create_recurring_booking", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_user_id", type = Long.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_court_id", type = Long.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_start_date", type = LocalDate.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_end_date", type = LocalDate.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_time_start", type = LocalDateTime.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_time_end", type = LocalDateTime.class),
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_day_of_week", type = String.class)
})
public class Booking extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", nullable = false, length = 20)
    @Builder.Default
    private BookingType bookingType = BookingType.SINGLE;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "time_start", nullable = false)
    private LocalDateTime timeStart;

    @Column(name = "time_end", nullable = false)
    private LocalDateTime timeEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "deposit_amount")
    private Double depositAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmed_by")
    private String confirmedBy;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "booking_code", unique = true, length = 50)
    private String bookingCode;

    @Column(name = "recurring_booking_id")
    private Long recurringBookingId;

    @Column(name = "discount_amount")
    @Builder.Default
    private Double discountAmount = 0.0;

    @Column(name = "final_amount")
    private Double finalAmount;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    // === HELPER METHODS ===

    @PrePersist
    public void generateBookingCode() {
        if (bookingCode == null) {
            bookingCode = "BK" + System.currentTimeMillis();
        }
        if (finalAmount == null) {
            finalAmount = totalAmount - (discountAmount != null ? discountAmount : 0.0);
        }
    }

    public void cancel(String cancelledBy, String reason) {
        this.status = BookingStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;
    }

    public void confirm(String confirmedBy) {
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.confirmedBy = confirmedBy;
    }

    public void complete() {
        this.status = BookingStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void checkIn() {
        this.checkInTime = LocalDateTime.now();
    }

    public void checkOut() {
        this.checkOutTime = LocalDateTime.now();
        if (this.status == BookingStatus.CONFIRMED) {
            this.complete();
        }
    }

    public Long getDurationInHours() {
        return java.time.Duration.between(timeStart, timeEnd).toHours();
    }

    public boolean isActive() {
        return status == BookingStatus.PENDING || status == BookingStatus.CONFIRMED;
    }
}
