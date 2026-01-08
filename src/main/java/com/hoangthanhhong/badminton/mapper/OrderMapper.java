// File: OrderMapper.java
package com.hoangthanhhong.badminton.mapper;

import com.hoangthanhhong.badminton.dto.order.OrderDTO;
import com.hoangthanhhong.badminton.dto.order.OrderDetailDTO;
import com.hoangthanhhong.badminton.entity.Order;
import com.hoangthanhhong.badminton.entity.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public OrderDTO toDTO(Order order) {
        if (order == null)
            return null;

        return OrderDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .userEmail(order.getUser().getEmail())
                .status(order.getStatus())
                .paymentStatus(order.getPaymentStatus())
                .paymentMethod(order.getPaymentMethod())
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .taxAmount(order.getTaxAmount())
                .discountAmount(order.getDiscountAmount())
                .finalAmount(order.getFinalAmount())
                .shippingName(order.getShippingName())
                .shippingPhone(order.getShippingPhone())
                .shippingAddress(order.getShippingAddress())
                .shippingCity(order.getShippingCity())
                .shippingDistrict(order.getShippingDistrict())
                .shippingWard(order.getShippingWard())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    public OrderDTO toDTOWithDetails(Order order) {
        OrderDTO dto = toDTO(order);

        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            dto.setOrderDetails(order.getOrderDetails().stream()
                    .map(this::toOrderDetailDTO)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public OrderDetailDTO toOrderDetailDTO(OrderDetail detail) {
        if (detail == null)
            return null;

        return OrderDetailDTO.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProduct().getName())
                .productImage(detail.getProduct().getImageUrl())
                .quantity(detail.getQuantity())
                .unitPrice(detail.getUnitPrice())
                .subtotal(detail.getSubtotal())
                .build();
    }
}
