package com.hoangthanhhong.badminton.enums;

public enum TournamentStatus {
    UPCOMING("Upcoming", "Sắp diễn ra"),
    REGISTRATION_OPEN("Registration Open", "Đang mở đăng ký"),
    REGISTRATION_CLOSED("Registration Closed", "Đã đóng đăng ký"),
    ONGOING("Ongoing", "Đang diễn ra"),
    COMPLETED("Completed", "Đã hoàn thành"),
    CANCELLED("Cancelled", "Đã hủy"),
    POSTPONED("Postponed", "Đã hoãn");

    private final String name;
    private final String displayName;

    TournamentStatus(String name, String displayName) {
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
