package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items", indexes = {
        @Index(name = "idx_cart_id", columnList = "cart_id"),
        @Index(name = "idx_product_id", columnList = "product_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Double subtotal;

    @Column(length = 100)
    private String variant;

    @Column(name = "variant_value", length = 100)
    private String variantValue;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // === HELPER METHODS ===

    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        this.subtotal = this.price * this.quantity;
    }

    public void updatePrice(Product product) {
        this.price = product.getCurrentPrice();
        calculateSubtotal();
    }

    public void increaseQuantity(Integer amount) {
        this.quantity += amount;
        calculateSubtotal();
    }

    public void decreaseQuantity(Integer amount) {
        if (this.quantity > amount) {
            this.quantity -= amount;
            calculateSubtotal();
        }
    }

    public boolean isAvailable() {
        return product.isInStock() && product.getStock() >= quantity;
    }
}
