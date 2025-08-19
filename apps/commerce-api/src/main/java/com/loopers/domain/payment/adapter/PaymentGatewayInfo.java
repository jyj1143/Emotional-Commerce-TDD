package com.loopers.domain.payment.adapter;

public record PaymentGatewayInfo(
) {
    // 결제 정보 확인
    public record TransactionDetail(
        String transactionKey,
        String orderId,
        String cardType,
        String cardNo,
        String amount,
        String status,
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
        String status,
        String reason
    ) {
    }

}
