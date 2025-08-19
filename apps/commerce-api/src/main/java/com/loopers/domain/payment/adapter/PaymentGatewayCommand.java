package com.loopers.domain.payment.adapter;

public record PaymentGatewayCommand() {
    // 결제 요청
    public record Payment(
        String orderId,
        String cardType,
        String cardNo,
        Long amount,
        String callbackUrl
    ) {

    }

}
