package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.DiscountType;
import com.hoangthanhhong.badminton.enums.PromotionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "promotions", indexes = {
        @Index(name = "idx_code", columnList = "code"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_dates", columnList = "start_date, end_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType; // PERCENTAGE, FIXED_AMOUNT

    @Column(name = "discount_value", nullable = false)
    private Double discountValue;

    @Column(name = "max_discount_amount")
    private Double maxDiscountAmount; // For percentage discounts

    @Column(name = "min_order_amount")
    private Double minOrderAmount; // Minimum order to apply

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PromotionStatus status = PromotionStatus.ACTIVE;

    @Column(name = "max_usage")
    private Integer maxUsage; // Total usage limit

    @Column(name = "current_usage")
    @Builder.Default
    private Integer currentUsage = 0;

    @Column(name = "max_usage_per_user")
    private Integer maxUsagePerUser;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true; // Can be seen by all users

    @Column(name = "is_auto_apply")
    @Builder.Default
    private Boolean isAutoApply = false; // Auto apply if conditions met

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "terms_and_conditions", columnDefinition = "TEXT")
    private String termsAndConditions;

    // === APPLICABILITY ===

    @Column(name = "applicable_to", length = 50)
    private String applicableTo; // BOOKING, ORDER, ALL

    @Column(name = "applicable_days")
    private String applicableDays; // JSON array: ["MONDAY", "TUESDAY"]

    @Column(name = "applicable_time_start")
    private java.time.LocalTime applicableTimeStart;

    @Column(name = "applicable_time_end")
    private java.time.LocalTime applicableTimeEnd;

    // === RELATIONSHIPS ===

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Booking> bookings = new ArrayList<>();

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PromotionUsage> usages = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "promotion_courts", joinColumns = @JoinColumn(name = "promotion_id"), inverseJoinColumns = @JoinColumn(name = "court_id"))
    @Builder.Default
    private List<Court> applicableCourts = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "promotion_products", joinColumns = @JoinColumn(name = "promotion_id"), inverseJoinColumns = @JoinColumn(name = "product_id"))
    @Builder.Default
    private List<Product> applicableProducts = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "promotion_users", joinColumns = @JoinColumn(name = "promotion_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private List<User> eligibleUsers = new ArrayList<>();

    // === HELPER METHODS ===

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return status == PromotionStatus.ACTIVE
                && !now.isBefore(startDate)
                && !now.isAfter(endDate);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(endDate);
    }

    public boolean hasReachedMaxUsage() {
        return maxUsage != null && currentUsage >= maxUsage;
    }

    public boolean canBeUsedBy(Long userId) {
        if (!isActive() || hasReachedMaxUsage()) {
            return false;
        }

        // Check if user-specific promotion
        if (!isPublic && !eligibleUsers.isEmpty()) {
            boolean isEligible = eligibleUsers.stream()
                    .anyMatch(u -> u.getId().equals(userId));
            if (!isEligible)
                return false;
        }

        // Check per-user usage limit
        if (maxUsagePerUser != null) {
            long userUsageCount = usages.stream()
                    .filter(u -> u.getUser().getId().equals(userId))
                    .count();
            if (userUsageCount >= maxUsagePerUser) {
                return false;
            }
        }

        return true;
    }

    public Double calculateDiscount(Double amount) {
        if (amount < (minOrderAmount != null ? minOrderAmount : 0)) {
            return 0.0;
        }

        Double discount;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = amount * (discountValue / 100);
            if (maxDiscountAmount != null && discount > maxDiscountAmount) {
                discount = maxDiscountAmount;
            }
        } else {
            discount = discountValue;
        }

        return Math.min(discount, amount);
    }

    public void incrementUsage() {
        this.currentUsage++;

        if (maxUsage != null && currentUsage >= maxUsage) {
            this.status = PromotionStatus.EXHAUSTED;
        }
    }

    public void decrementUsage() {
        if (this.currentUsage > 0) {
            this.currentUsage--;
        }
    }

    public void activate() {
        this.status = PromotionStatus.ACTIVE;
    }

    public void deactivate() {
        this.status = PromotionStatus.INACTIVE;
    }

    public void expire() {
        this.status = PromotionStatus.EXPIRED;
    }

    public Integer getRemainingUsage() {
        if (maxUsage == null)
            return null;
        return Math.max(0, maxUsage - currentUsage);
    }

    public String getDiscountDisplay() {
        if (discountType == DiscountType.PERCENTAGE) {
            return String.format("%.0f%%", discountValue);
        } else {
            return String.format("%.0f VND", discountValue);
        }
    }
}
