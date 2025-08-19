package com.loopers.infrastructure.external.payment.dto;

public record PgClientV1Dto() {

    // 결제 요청
    public record PaymentRequest(
            String orderId,
            String cardType,
            String cardNo,
            Long amount,
            String callbackUrl
    ) {
    }

    // 결제 응답
    public record TransactionResponse(
            String transactionKey,
            String status,
            String reason
    ) {
    }

    // 결제 정보 확인 응답
    public record TransactionDetailResponse(
        String transactionKey,
        String orderId,
        String cardType,
        String cardNo,
        String amount,
        String status,
        String reason
    ){}

    //  주문에 엮인 결제 정보 조회 응답
    public record OrderResponse(
        String orderId,
        String transactionId
    ) {
    }
}
