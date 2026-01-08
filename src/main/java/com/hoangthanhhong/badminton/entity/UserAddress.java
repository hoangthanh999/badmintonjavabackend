package com.hoangthanhhong.badminton.entity;

import com.hoangthanhhong.badminton.entity.base.BaseEntity;
import com.hoangthanhhong.badminton.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_addresses", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_is_default", columnList = "is_default"),
        @Index(name = "idx_type", columnList = "type")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAddress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    @Builder.Default
    private AddressType type = AddressType.HOME;

    @Column(name = "receiver_name", length = 100, nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", length = 20, nullable = false)
    private String receiverPhone;

    @Column(name = "address_line1", columnDefinition = "TEXT", nullable = false)
    private String addressLine1; // Số nhà, tên đường

    @Column(name = "address_line2", columnDefinition = "TEXT")
    private String addressLine2; // Thông tin bổ sung

    @Column(length = 100)
    private String ward; // Phường/Xã

    @Column(length = 100)
    private String district; // Quận/Huyện

    @Column(length = 100, nullable = false)
    private String city; // Tỉnh/Thành phố

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 50)
    @Builder.Default
    private String country = "Vietnam";

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(columnDefinition = "TEXT")
    private String notes; // Ghi chú cho shipper

    @Column(name = "label", length = 100)
    private String label; // "Nhà riêng", "Công ty", "Nhà bạn gái", etc.

    // === HELPER METHODS ===

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null)
            sb.append(addressLine1);
        if (addressLine2 != null)
            sb.append(", ").append(addressLine2);
        if (ward != null)
            sb.append(", ").append(ward);
        if (district != null)
            sb.append(", ").append(district);
        if (city != null)
            sb.append(", ").append(city);
        if (postalCode != null)
            sb.append(" ").append(postalCode);
        return sb.toString();
    }

    public void setAsDefault() {
        this.isDefault = true;
    }

    public void unsetDefault() {
        this.isDefault = false;
    }

    public boolean hasCoordinates() {
        return latitude != null && longitude != null;
    }
}