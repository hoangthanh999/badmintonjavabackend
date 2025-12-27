package com.hoangthanhhong.badminton.enums;

public enum MessageType {
    TEXT("Text", "Văn bản"),
    IMAGE("Image", "Hình ảnh"),
    VIDEO("Video", "Video"),
    AUDIO("Audio", "Âm thanh"),
    FILE("File", "Tệp đính kèm"),
    LOCATION("Location", "Vị trí"),
    STICKER("Sticker", "Nhãn dán"),
    SYSTEM("System", "Hệ thống"),
    LINK("Link", "Liên kết"),
    CONTACT("Contact", "Liên hệ");

    private final String name;
    private final String displayName;

    MessageType(String name, String displayName) {
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
