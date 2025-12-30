package com.hoangthanhhong.badminton.enums;

public enum ParticipantStatus {
    REGISTERED("Registered", "Đã đăng ký"),
    CONFIRMED("Confirmed", "Đã xác nhận"),
    CHECKED_IN("Checked In", "Đã check-in"),
    WITHDRAWN("Withdrawn", "Đã rút lui"),
    DISQUALIFIED("Disqualified", "Bị loại"),
    ELIMINATED("Eliminated", "Bị loại khỏi giải");

    private final String name;
    private final String displayName;

    ParticipantStatus(String name, String displayName) {
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
