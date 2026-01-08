package com.hoangthanhhong.badminton.enums;

import lombok.Getter;

@Getter
public enum ReferralStatus {
    PENDING("Chờ xử lý", "Đã gửi lời mời nhưng chưa đăng ký"),
    REGISTERED("Đã đăng ký", "Người được mời đã đăng ký tài khoản"),
    COMPLETED("Hoàn thành", "Đã đủ điều kiện nhận thưởng"),
    CLAIMED("Đã nhận thưởng", "Phần thưởng đã được claim"),
    EXPIRED("Hết hạn", "Đã quá thời hạn sử dụng"),
    CANCELLED("Đã hủy", "Referral bị hủy"),
    REJECTED("Bị từ chối", "Không đủ điều kiện");

    private final String displayName;
    private final String description;

    ReferralStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
}