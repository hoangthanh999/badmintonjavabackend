package com.hoangthanhhong.badminton.enums;

public enum MaintenanceStatus {
    SCHEDULED("Scheduled", "Đã lên lịch"),
    IN_PROGRESS("In Progress", "Đang thực hiện"),
    COMPLETED("Completed", "Hoàn thành"),
    CANCELLED("Cancelled", "Đã hủy"),
    POSTPONED("Postponed", "Đã hoãn"),
    ON_HOLD("On Hold", "Tạm dừng");

    private final String name;
    private final String displayName;

    MaintenanceStatus(String name, String displayName) {
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
