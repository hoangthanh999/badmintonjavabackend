package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "payment_methods")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethod extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code; // VNPAY, MOMO, ZALOPAY, CASH, BANK_TRANSFER

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String logo;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "transaction_fee")
    @Builder.Default
    private Double transactionFee = 0.0;

    @Column(name = "fee_type", length = 20)
    private String feeType; // FIXED, PERCENTAGE

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "method", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();
}
