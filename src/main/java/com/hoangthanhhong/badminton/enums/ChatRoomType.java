package com.hoangthanhhong.badminton.enums;

public enum ChatRoomType {
    DIRECT("Direct Message", "Chat 1-1"),
    GROUP("Group Chat", "Chat nhóm"),
    SUPPORT("Support Chat", "Chat hỗ trợ"),
    TOURNAMENT("Tournament Chat", "Chat giải đấu"),
    BOOKING("Booking Chat", "Chat đặt sân"),
    ANNOUNCEMENT("Announcement", "Thông báo");

    private final String name;
    private final String displayName;

    ChatRoomType(String name, String displayName) {
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
