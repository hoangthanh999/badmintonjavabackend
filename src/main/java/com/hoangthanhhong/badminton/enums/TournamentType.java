package com.hoangthanhhong.badminton.enums;

public enum TournamentType {
    SINGLES("Singles", "Đơn"),
    DOUBLES("Doubles", "Đôi"),
    MIXED_DOUBLES("Mixed Doubles", "Đôi nam nữ"),
    TEAM("Team", "Đồng đội");

    private final String name;
    private final String displayName;

    TournamentType(String name, String displayName) {
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
