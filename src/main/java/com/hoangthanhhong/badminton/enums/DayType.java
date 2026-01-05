package com.hoangthanhhong.badminton.enums;

public enum DayType {
    WEEKDAY("Weekday", "Ngày thường"),
    WEEKEND("Weekend", "Cuối tuần"),
    HOLIDAY("Holiday", "Ngày lễ");

    private final String name;
    private final String displayName;

    DayType(String name, String displayName) {
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
