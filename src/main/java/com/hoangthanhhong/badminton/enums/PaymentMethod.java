package com.hoangthanhhong.badminton.enums;

public enum PaymentMethod {
    CASH("Cash", "Tiền mặt"),
    BANK_TRANSFER("Bank Transfer", "Chuyển khoản ngân hàng"),
    CREDIT_CARD("Credit Card", "Thẻ tín dụng"),
    DEBIT_CARD("Debit Card", "Thẻ ghi nợ"),
    MOMO("MoMo", "Ví MoMo"),
    ZALOPAY("ZaloPay", "Ví ZaloPay"),
    VNPAY("VNPay", "VNPay"),
    PAYPAL("PayPal", "PayPal"),
    COD("COD", "Thanh toán khi nhận hàng");

    private final String displayName;
    private final String displayNameVi;

    PaymentMethod(String displayName, String displayNameVi) {
        this.displayName = displayName;
        this.displayNameVi = displayNameVi;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDisplayNameVi() {
        return displayNameVi;
    }
}
