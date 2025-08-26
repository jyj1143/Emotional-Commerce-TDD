package com.loopers.domain.payment.adapter;

import com.loopers.domain.payment.enums.CardType;

public record PaymentGatewayCommand() {
    // 결제 요청
    public record Payment(
        Long orderId,
        CardType cardType,
        String cardNo,
        Long amount
    ) {

    }

}
