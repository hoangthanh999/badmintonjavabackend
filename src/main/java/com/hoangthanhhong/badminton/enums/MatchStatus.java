package com.hoangthanhhong.badminton.enums;

public enum MatchStatus {
    SCHEDULED("Scheduled", "Đã lên lịch"),
    IN_PROGRESS("In Progress", "Đang diễn ra"),
    COMPLETED("Completed", "Đã hoàn thành"),
    CANCELLED("Cancelled", "Đã hủy"),
    POSTPONED("Postponed", "Đã hoãn"),
    WALKOVER("Walkover", "Thắng do đối thủ bỏ cuộc");

    private final String name;
    private final String displayName;

    MatchStatus(String name, String displayName) {
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
