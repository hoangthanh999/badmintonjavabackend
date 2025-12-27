package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wishlist_items", uniqueConstraints = @UniqueConstraint(columnNames = { "wishlist_id",
        "product_id" }), indexes = {
                @Index(name = "idx_wishlist_id", columnList = "wishlist_id"),
                @Index(name = "idx_product_id", columnList = "product_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "added_price")
    private Double addedPrice; // Price when added to wishlist

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "notify_on_sale")
    @Builder.Default
    private Boolean notifyOnSale = false;

    @Column(name = "notify_on_stock")
    @Builder.Default
    private Boolean notifyOnStock = false;

    // === HELPER METHODS ===

    public boolean isPriceDropped() {
        if (addedPrice == null)
            return false;
        return product.getCurrentPrice() < addedPrice;
    }

    public Double getPriceDropAmount() {
        if (addedPrice == null)
            return 0.0;
        return addedPrice - product.getCurrentPrice();
    }

    public Double getPriceDropPercentage() {
        if (addedPrice == null || addedPrice == 0)
            return 0.0;
        return ((addedPrice - product.getCurrentPrice()) / addedPrice) * 100;
    }
}
