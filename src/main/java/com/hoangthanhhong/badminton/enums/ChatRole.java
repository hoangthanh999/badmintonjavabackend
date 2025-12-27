package com.hoangthanhhong.badminton.enums;

public enum ChatRole {
    OWNER("Owner", "Chủ phòng"),
    ADMIN("Admin", "Quản trị viên"),
    MODERATOR("Moderator", "Người kiểm duyệt"),
    MEMBER("Member", "Thành viên");

    private final String name;
    private final String displayName;

    ChatRole(String name, String displayName) {
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
