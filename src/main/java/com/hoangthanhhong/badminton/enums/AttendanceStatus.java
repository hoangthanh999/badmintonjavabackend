package com.hoangthanhhong.badminton.enums;

public enum AttendanceStatus {
    PRESENT("Present", "Có mặt"),
    ABSENT("Absent", "Vắng mặt"),
    LEAVE("Leave", "Nghỉ phép"),
    HALF_DAY("Half Day", "Nửa ngày"),
    LATE("Late", "Đi muộn"),
    EARLY_DEPARTURE("Early Departure", "Về sớm"),
    HOLIDAY("Holiday", "Ngày lễ"),
    WEEKEND("Weekend", "Cuối tuần"),
    WORK_FROM_HOME("Work From Home", "Làm việc từ xa");

    private final String name;
    private final String displayName;

    AttendanceStatus(String name, String displayName) {
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
