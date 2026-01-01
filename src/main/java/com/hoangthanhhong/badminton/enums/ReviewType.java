package com.hoangthanhhong.badminton.enums;

public enum ReviewType {
    COURT("Court", "Đánh giá sân"),
    PRODUCT("Product", "Đánh giá sản phẩm"),
    SERVICE("Service", "Đánh giá dịch vụ"),
    ORDER("Order", "Đánh giá đơn hàng"),
    BOOKING("Booking", "Đánh giá đặt sân"),
    TOURNAMENT("Tournament", "Đánh giá giải đấu");

    private final String name;
    private final String displayName;

    ReviewType(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
}
