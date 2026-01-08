package com.hoangthanhhong.badminton.dto.order;

import com.hoangthanhhong.badminton.enums.OrderStatus;
import com.hoangthanhhong.badminton.enums.PaymentMethod;
import com.hoangthanhhong.badminton.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {
    private Long id;
    private String orderCode;

    // User
    private Long userId;
    private String userName;
    private String userEmail;

    // Status
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private PaymentMethod paymentMethod;

    // Amounts
    private Double subtotal;
    private Double shippingFee;
    private Double taxAmount;
    private Double discountAmount;
    private Double finalAmount;

    // Shipping
    private String shippingName;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingCity;
    private String shippingDistrict;
    private String shippingWard;

    // Notes
    private String notes;

    // Order details
    private List<OrderDetailDTO> orderDetails;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
