package com.hoangthanhhong.badminton.service.payment;
// PaymentStrategy.java (Interface - Trừu tượng)

import com.hoangthanhhong.badminton.dto.request.payment.PaymentRequest;
import com.hoangthanhhong.badminton.dto.response.payment.PaymentResponse;

public interface PaymentStrategy {
    PaymentResponse processPayment(PaymentRequest request);

    boolean verifyPayment(String transactionId);

    String getPaymentMethodName();
}

// VNPayStrategy.java (Đa hình)
@Service
public class VNPayStrategy implements PaymentStrategy {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // VNPay specific logic
        return PaymentResponse.builder()
                .paymentUrl("https://vnpay.vn/...")
                .method("VNPAY")
                .build();
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        // VNPay verification
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "VNPAY";
    }
}

// MoMoStrategy.java (Đa hình)
@Service
public class MoMoStrategy implements PaymentStrategy {

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        // MoMo specific logic
        return PaymentResponse.builder()
                .paymentUrl("https://momo.vn/...")
                .method("MOMO")
                .build();
    }

    @Override
    public boolean verifyPayment(String transactionId) {
        // MoMo verification
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "MOMO";
    }
}

// PaymentContext.java (Context)
@Service
public class PaymentContext {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentContext(List<PaymentStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        PaymentStrategy::getPaymentMethodName,
                        Function.identity()));
    }

    public PaymentResponse executePayment(String method, PaymentRequest request) {
        PaymentStrategy strategy = strategies.get(method.toUpperCase());
        if (strategy == null) {
            throw new BadRequestException("Payment method not supported: " + method);
        }
        return strategy.processPayment(request);
    }
}
