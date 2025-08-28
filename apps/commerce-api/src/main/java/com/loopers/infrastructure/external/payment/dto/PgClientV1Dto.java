package com.loopers.infrastructure.external.payment.dto;

import com.loopers.domain.payment.adapter.PaymentGatewayInfo;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentStatus;

public record PgClientV1Dto() {

    // 결제 요청
    public record PaymentRequest(
            Long orderId,
            String cardType,
            String cardNo,
            Long amount,
            String callbackUrl
    ) {
    }

    // 결제 응답
    public record TransactionResponse(
            String transactionKey,
            TransactionStatusDto status,
            String reason
    ) {

        public PaymentGatewayInfo.Transaction toTransaction() {
            return new PaymentGatewayInfo.Transaction(
                transactionKey,
                status.toPaymentStatus(),
                reason,
                true
            );
        }
    }

    // 결제 정보 확인 응답
    public record TransactionDetailResponse(
        String transactionKey,
        String orderId,
        CardTypeDto cardType,
        String cardNo,
        String amount,
        TransactionStatusDto status,
        String reason
    ){
        public PaymentGatewayInfo.TransactionDetail toTransactionDetail() {
            return new PaymentGatewayInfo.TransactionDetail(
                transactionKey,
                orderId,
                cardType.toCardType(),
                cardNo,
                amount,
                status.toPaymentStatus(),
                reason
            );
        }


    }

    //  주문에 엮인 결제 정보 조회 응답
    public record OrderResponse(
        String orderId,
        String transactionId
    ) {
        public PaymentGatewayInfo.Order toOrder() {
            return new PaymentGatewayInfo.Order(
                orderId,
                transactionId
            );
        }
    }

    public enum CardTypeDto {
        SAMSUNG,
        KB,
        HYUNDAI,
        ;

        public CardType toCardType() {
            return switch (this) {
                case SAMSUNG -> CardType.SAMSUNG;
                case KB -> CardType.KB;
                case HYUNDAI -> CardType.HYUNDAI;
            };
        }
    }


    public enum TransactionStatusDto {
        PENDING,
        SUCCESS,
        FAILED,
        ;

        public PaymentStatus toPaymentStatus() {
            return switch (this) {
                case PENDING -> PaymentStatus.PENDING;
                case SUCCESS -> PaymentStatus.COMPLETED;
                case FAILED -> PaymentStatus.FAILED;
            };
        }
    }
}
