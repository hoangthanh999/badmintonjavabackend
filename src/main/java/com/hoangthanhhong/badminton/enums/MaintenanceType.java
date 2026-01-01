package com.hoangthanhhong.badminton.enums;

public enum MaintenanceType {
    PREVENTIVE("Preventive", "Bảo trì định kỳ"),
    CORRECTIVE("Corrective", "Sửa chữa"),
    EMERGENCY("Emergency", "Khẩn cấp"),
    INSPECTION("Inspection", "Kiểm tra"),
    CLEANING("Cleaning", "Vệ sinh"),
    UPGRADE("Upgrade", "Nâng cấp"),
    REPLACEMENT("Replacement", "Thay thế"),
    CALIBRATION("Calibration", "Hiệu chỉnh");

    private final String name;
    private final String displayName;

    MaintenanceType(String name, String displayName) {
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
