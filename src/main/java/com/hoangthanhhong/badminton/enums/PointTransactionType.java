package com.hoangthanhhong.badminton.enums;

public enum PointTransactionType {
    // Earning
    BOOKING_COMPLETED("Booking Completed", "Hoàn thành đặt sân", true),
    ORDER_COMPLETED("Order Completed", "Hoàn thành đơn hàng", true),
    REVIEW_SUBMITTED("Review Submitted", "Gửi đánh giá", true),
    REFERRAL_SUCCESS("Referral Success", "Giới thiệu thành công", true),
    REFERRAL("Referral", "Giới thiệu", true),
    BIRTHDAY_BONUS("Birthday Bonus", "Thưởng sinh nhật", true),
    SIGN_UP_BONUS("Sign Up Bonus", "Thưởng đăng ký", true),
    ADMIN_ADJUSTMENT("Admin Adjustment", "Điều chỉnh bởi admin", true),
    PROMOTION_BONUS("Promotion Bonus", "Thưởng khuyến mãi", true),

    // Spending
    REDEEM_DISCOUNT("Redeem Discount", "Đổi giảm giá", false),
    REDEEM_VOUCHER("Redeem Voucher", "Đổi voucher", false),
    REDEEM_GIFT("Redeem Gift", "Đổi quà", false),
    ADJUSTMENT("Adjustment", "Điều chỉnh", false),
    // Other
    EXPIRED("Expired", "Hết hạn", false),
    REVERSED("Reversed", "Hoàn lại", false);

    private final String name;
    private final String displayName;
    private final boolean isEarning;

    PointTransactionType(String name, String displayName, boolean isEarning) {
        this.name = name;
        this.displayName = displayName;
        this.isEarning = isEarning;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isEarning() {
        return isEarning;
    }

    public boolean isSpending() {
        return !isEarning;
    }
}
