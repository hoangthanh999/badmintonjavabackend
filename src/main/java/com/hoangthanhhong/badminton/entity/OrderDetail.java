package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_details", indexes = {
        @Index(name = "idx_order_id", columnList = "order_id"),
        @Index(name = "idx_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_sku", length = 100)
    private String productSku;

    @Column(name = "product_image", length = 500)
    private String productImage;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column(name = "discount_amount")
    @Builder.Default
    private Double discountAmount = 0.0;

    @Column(nullable = false)
    private Double subtotal;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_reviewed")
    @Builder.Default
    private Boolean isReviewed = false;

    // === VARIANT INFORMATION (if applicable) ===

    @Column(length = 100)
    private String variant; // Size, Color, etc.

    @Column(name = "variant_value", length = 100)
    private String variantValue;

    // === HELPER METHODS ===

    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        this.subtotal = (this.price * this.quantity) - (this.discountAmount != null ? this.discountAmount : 0.0);
    }

    public void updateFromProduct(Product product) {
        this.productName = product.getName();
        this.productSku = product.getSku();
        this.productImage = product.getImage();
        this.price = product.getCurrentPrice();
    }

    public Double getTotalPrice() {
        return price * quantity;
    }

    public Double getFinalPrice() {
        return subtotal;
    }
}
