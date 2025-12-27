package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "wishlists", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wishlist extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(length = 100)
    private String name;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = false;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<WishlistItem> items = new ArrayList<>();

    // === HELPER METHODS ===

    public void addItem(WishlistItem item) {
        if (!items.contains(item)) {
            items.add(item);
            item.setWishlist(this);
        }
    }

    public void removeItem(WishlistItem item) {
        items.remove(item);
        item.setWishlist(null);
    }

    public boolean containsProduct(Long productId) {
        return items.stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
    }

    public Integer getTotalItems() {
        return items.size();
    }
}
