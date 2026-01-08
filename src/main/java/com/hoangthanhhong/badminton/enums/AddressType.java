package com.hoangthanhhong.badminton.enums;

import lombok.Getter;

@Getter
public enum AddressType {
    HOME("Nhà riêng", "🏠"),
    OFFICE("Văn phòng", "🏢"),
    APARTMENT("Chung cư", "🏘️"),
    PICKUP_POINT("Điểm lấy hàng", "📦"),
    OTHER("Khác", "📍");

    private final String displayName;
    private final String icon;

    AddressType(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }
}