package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
        @Index(name = "idx_slug", columnList = "slug"),
        @Index(name = "idx_category", columnList = "category_id"),
        @Index(name = "idx_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProductCategory category;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(unique = true, length = 250)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(name = "sale_price")
    private Double salePrice;

    @Column(nullable = false)
    @Builder.Default
    private Integer stock = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.ACTIVE;

    @Column(length = 500)
    private String image;

    @Column(length = 100)
    private String sku;

    @Column(length = 100)
    private String brand;

    @Column(name = "weight")
    private Double weight; // kg

    @Column(name = "total_sold")
    @Builder.Default
    private Integer totalSold = 0;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    // === HELPER METHODS ===

    public Double getCurrentPrice() {
        return salePrice != null && salePrice > 0 ? salePrice : price;
    }

    public boolean isInStock() {
        return stock > 0;
    }

    public void decreaseStock(Integer quantity) {
        if (stock >= quantity) {
            stock -= quantity;
            totalSold += quantity;
        } else {
            throw new IllegalStateException("Not enough stock");
        }
    }

    public void increaseStock(Integer quantity) {
        stock += quantity;
    }
}
