package com.hoangthanhhong.badminton.enums;

public enum NotificationType {
    // Booking
    BOOKING_CONFIRMED("Booking Confirmed", "Xác nhận đặt sân"),
    BOOKING_CANCELLED("Booking Cancelled", "Hủy đặt sân"),
    BOOKING_REMINDER("Booking Reminder", "Nhắc nhở đặt sân"),
    BOOKING_COMPLETED("Booking Completed", "Hoàn thành đặt sân"),

    // Order
    ORDER_PLACED("Order Placed", "Đặt hàng thành công"),
    ORDER_CONFIRMED("Order Confirmed", "Xác nhận đơn hàng"),
    ORDER_SHIPPED("Order Shipped", "Đơn hàng đã gửi"),
    ORDER_DELIVERED("Order Delivered", "Đơn hàng đã giao"),
    ORDER_CANCELLED("Order Cancelled", "Hủy đơn hàng"),

    // Payment
    PAYMENT_SUCCESS("Payment Success", "Thanh toán thành công"),
    PAYMENT_FAILED("Payment Failed", "Thanh toán thất bại"),
    REFUND_PROCESSED("Refund Processed", "Hoàn tiền thành công"),

    // Tournament
    TOURNAMENT_REGISTRATION("Tournament Registration", "Đăng ký giải đấu"),
    TOURNAMENT_STARTED("Tournament Started", "Giải đấu bắt đầu"),
    TOURNAMENT_MATCH("Tournament Match", "Trận đấu sắp diễn ra"),
    TOURNAMENT_RESULT("Tournament Result", "Kết quả trận đấu"),

    // Promotion
    PROMOTION_AVAILABLE("Promotion Available", "Khuyến mãi mới"),
    PROMOTION_EXPIRING("Promotion Expiring", "Khuyến mãi sắp hết hạn"),

    // Loyalty
    POINTS_EARNED("Points Earned", "Nhận điểm thưởng"),
    POINTS_EXPIRING("Points Expiring", "Điểm sắp hết hạn"),
    TIER_UPGRADED("Tier Upgraded", "Nâng hạng thành viên"),

    // System
    SYSTEM_ANNOUNCEMENT("System Announcement", "Thông báo hệ thống"),
    MAINTENANCE("Maintenance", "Bảo trì hệ thống"),

    // Social
    NEW_MESSAGE("New Message", "Tin nhắn mới"),
    NEW_FOLLOWER("New Follower", "Người theo dõi mới"),
    MENTION("Mention", "Được nhắc đến"),

    // Review
    REVIEW_REPLY("Review Reply", "Phản hồi đánh giá"),
    REVIEW_HELPFUL("Review Helpful", "Đánh giá hữu ích");

    private final String name;
    private final String displayName;

    NotificationType(String name, String displayName) {
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
