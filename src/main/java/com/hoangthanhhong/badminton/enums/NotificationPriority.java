package com.hoangthanhhong.badminton.enums;

public enum NotificationPriority {
    LOW("Low", "Thấp"),
    NORMAL("Normal", "Bình thường"),
    HIGH("High", "Cao"),
    URGENT("Urgent", "Khẩn cấp");

    private final String name;
    private final String displayName;

    NotificationPriority(String name, String displayName) {
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
