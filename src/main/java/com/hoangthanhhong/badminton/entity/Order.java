package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.OrderStatus;
import com.hoangthanhhong.badminton.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_order_code", columnList = "order_code"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "order_code", unique = true, nullable = false, length = 50)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    @Column(name = "discount_amount")
    @Builder.Default
    private Double discountAmount = 0.0;

    @Column(name = "shipping_fee")
    @Builder.Default
    private Double shippingFee = 0.0;

    @Column(name = "tax_amount")
    @Builder.Default
    private Double taxAmount = 0.0;

    @Column(name = "final_amount", nullable = false)
    private Double finalAmount;
    @Column(name = "subtotal")
    private Double subtotal;
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // === SHIPPING INFORMATION ===

    @Column(name = "shipping_name", nullable = false, length = 100)
    private String shippingName;

    @Column(name = "shipping_phone", nullable = false, length = 20)
    private String shippingPhone;

    @Column(name = "shipping_email", length = 100)
    private String shippingEmail;

    @Column(name = "shipping_address", nullable = false, columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "shipping_city", length = 100)
    private String shippingCity;

    @Column(name = "shipping_district", length = 100)
    private String shippingDistrict;

    @Column(name = "shipping_ward", length = 100)
    private String shippingWard;

    @Column(name = "shipping_postal_code", length = 20)
    private String shippingPostalCode;

    // === ORDER TRACKING ===

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "shipping_provider", length = 50)
    private String shippingProvider; // GHN, GHTK, VNPost, etc.

    @Column(name = "estimated_delivery_date")
    private LocalDateTime estimatedDeliveryDate;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    // === ORDER NOTES ===

    @Column(name = "customer_notes", columnDefinition = "TEXT")
    private String customerNotes;
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "confirmed_by")
    private String confirmedBy;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // === INVOICE ===

    @Column(name = "invoice_number", unique = true, length = 50)
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDateTime invoiceDate;

    @Column(name = "tax_code", length = 50)
    private String taxCode; // Mã số thuế (nếu xuất hóa đơn)

    @Column(name = "company_name", length = 200)
    private String companyName; // Tên công ty (nếu xuất hóa đơn)

    @Column(name = "company_address", columnDefinition = "TEXT")
    private String companyAddress;

    // === RELATIONSHIPS ===
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 50)
    private com.hoangthanhhong.badminton.enums.PaymentMethod paymentMethod;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderStatusHistory> statusHistories = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;

    // === HELPER METHODS ===

    @PrePersist
    public void generateOrderCode() {
        if (orderCode == null) {
            orderCode = "ORD" + System.currentTimeMillis();
        }
        if (finalAmount == null) {
            calculateFinalAmount();
        }
    }

    public void calculateFinalAmount() {
        this.finalAmount = this.totalAmount
                - (this.discountAmount != null ? this.discountAmount : 0.0)
                + (this.shippingFee != null ? this.shippingFee : 0.0)
                + (this.taxAmount != null ? this.taxAmount : 0.0);
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
        orderDetail.setOrder(this);
        recalculateTotalAmount();
    }

    public void removeOrderDetail(OrderDetail orderDetail) {
        orderDetails.remove(orderDetail);
        orderDetail.setOrder(null);
        recalculateTotalAmount();
    }

    public void recalculateTotalAmount() {
        this.totalAmount = orderDetails.stream()
                .mapToDouble(OrderDetail::getSubtotal)
                .sum();
        calculateFinalAmount();
    }

    public void confirm(String confirmedBy) {
        this.status = OrderStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        this.confirmedBy = confirmedBy;
        addStatusHistory(OrderStatus.CONFIRMED, "Order confirmed", confirmedBy);
    }

    public void ship(String trackingNumber, String shippingProvider) {
        this.status = OrderStatus.SHIPPING;
        this.shippedAt = LocalDateTime.now();
        this.trackingNumber = trackingNumber;
        this.shippingProvider = shippingProvider;
        addStatusHistory(OrderStatus.SHIPPING, "Order shipped with tracking: " + trackingNumber, null);
    }

    public void deliver() {
        this.status = OrderStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
        addStatusHistory(OrderStatus.DELIVERED, "Order delivered successfully", null);
    }

    public void complete() {
        this.status = OrderStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        addStatusHistory(OrderStatus.COMPLETED, "Order completed", null);
    }

    public void cancel(String cancelledBy, String reason) {
        this.status = OrderStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = cancelledBy;
        this.cancellationReason = reason;
        addStatusHistory(OrderStatus.CANCELLED, "Order cancelled: " + reason, cancelledBy);
    }

    public void reject(String rejectedBy, String reason) {
        this.status = OrderStatus.REJECTED;
        this.cancelledAt = LocalDateTime.now();
        this.cancelledBy = rejectedBy;
        this.cancellationReason = reason;
        addStatusHistory(OrderStatus.REJECTED, "Order rejected: " + reason, rejectedBy);
    }

    public void returnOrder(String reason) {
        this.status = OrderStatus.RETURNED;
        this.cancellationReason = reason;
        addStatusHistory(OrderStatus.RETURNED, "Order returned: " + reason, null);
    }

    public void refund() {
        this.status = OrderStatus.REFUNDED;
        this.paymentStatus = PaymentStatus.REFUNDED;
        addStatusHistory(OrderStatus.REFUNDED, "Order refunded", null);
    }

    private void addStatusHistory(OrderStatus status, String notes, String changedBy) {
        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(this)
                .status(status)
                .notes(notes)
                .changedBy(changedBy)
                .changedAt(LocalDateTime.now())
                .build();
        this.statusHistories.add(history);
    }

    public Integer getTotalItems() {
        return orderDetails.stream()
                .mapToInt(OrderDetail::getQuantity)
                .sum();
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    public boolean canBeShipped() {
        return status == OrderStatus.CONFIRMED && paymentStatus == PaymentStatus.PAID;
    }

    public boolean isActive() {
        return status != OrderStatus.CANCELLED
                && status != OrderStatus.REJECTED
                && status != OrderStatus.COMPLETED;
    }

    public void generateInvoice() {
        if (invoiceNumber == null) {
            this.invoiceNumber = "INV" + System.currentTimeMillis();
            this.invoiceDate = LocalDateTime.now();
        }
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public com.hoangthanhhong.badminton.enums.PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(com.hoangthanhhong.badminton.enums.PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
