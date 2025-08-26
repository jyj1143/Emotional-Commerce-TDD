package com.loopers.domain.payment.adapter;

import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentStatus;

public record PaymentGatewayInfo(
) {
    // 결제 정보 확인
    public record TransactionDetail(
        String transactionKey,
        String orderId,
        CardType cardType,
        String cardNo,
        String amount,
        PaymentStatus status,
        String reason
    ){}

    //  주문에 엮인 결제 정보 조회
    public record Order(
        String orderId,
        String transactionId
    ) {
    }

    // 결제 응답
    public record Transaction(
        String transactionKey,
        PaymentStatus status,
        String reason
    ) {
    }

}
