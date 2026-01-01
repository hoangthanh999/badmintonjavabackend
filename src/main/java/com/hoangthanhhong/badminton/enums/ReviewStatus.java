package com.hoangthanhhong.badminton.enums;

public enum ReviewStatus {
    PENDING("Pending", "Chờ duyệt"),
    APPROVED("Approved", "Đã duyệt"),
    REJECTED("Rejected", "Bị từ chối"),
    SPAM("Spam", "Spam"),
    HIDDEN("Hidden", "Đã ẩn");

    private final String name;
    private final String displayName;

    ReviewStatus(String name, String displayName) {
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
