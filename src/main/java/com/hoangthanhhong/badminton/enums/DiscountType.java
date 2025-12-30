package com.hoangthanhhong.badminton.enums;

public enum DiscountType {
    PERCENTAGE("Percentage", "Giảm theo %"),
    FIXED_AMOUNT("Fixed Amount", "Giảm cố định");

    private final String name;
    private final String displayName;

    DiscountType(String name, String displayName) {
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
