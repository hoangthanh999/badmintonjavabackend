package com.hoangthanhhong.badminton.enums;

public enum PromotionStatus {
    ACTIVE("Active", "Đang hoạt động"),
    INACTIVE("Inactive", "Không hoạt động"),
    EXPIRED("Expired", "Đã hết hạn"),
    EXHAUSTED("Exhausted", "Đã hết lượt sử dụng"),
    SCHEDULED("Scheduled", "Đã lên lịch");

    private final String name;
    private final String displayName;

    PromotionStatus(String name, String displayName) {
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
