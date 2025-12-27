package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_booking_id", columnList = "booking_id"),
        @Index(name = "idx_transaction_id", columnList = "transaction_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "method_id", nullable = false)
    private PaymentMethod method;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transaction_id", unique = true, length = 200)
    private String transactionId;

    @Column(name = "payment_url", length = 500)
    private String paymentUrl;

    @Column(name = "payment_date")
    private java.time.LocalDateTime paymentDate;

    @Column(name = "response_code", length = 50)
    private String responseCode;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    @Column(name = "bank_code", length = 50)
    private String bankCode;

    @Column(name = "card_type", length = 50)
    private String cardType;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "refunded_at")
    private java.time.LocalDateTime refundedAt;

    // === HELPER METHODS ===

    public void markAsPaid() {
        this.status = PaymentStatus.PAID;
        this.paymentDate = java.time.LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = PaymentStatus.FAILED;
        this.responseMessage = errorMessage;
    }

    public void refund(Double amount) {
        this.status = PaymentStatus.REFUNDED;
        this.refundAmount = amount;
        this.refundedAt = java.time.LocalDateTime.now();
    }
}
