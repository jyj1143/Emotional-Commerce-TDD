package com.loopers.domain.payment.dto;

import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.PaymentStatus;

public record PaymentCommand() {

    public record Ready(
        Long userId,
        Long orderId,
        Long amount
    ) {

    }

    public record Pay(
            Long userId,
            Long orderId,
            PaymentMethod method,
            Long amount
    ) {

    }

    public record Synchronize(
        String transactionKey,
        String orderId,
        CardType cardType,
        String cardNo,
        Long amount,
        PaymentStatus status,
        String reason
    ) {

    }

    public record ReadyTransaction(
        Long paymentId,
        Long orderId,
        String transactionKey,
        PaymentStatus paymentStatus,
        Long amount,
        CardType cardType,
        String cardNumber
    ) {
    }

    public record Success(
        Long orderId,
        String transactionKey
    ) {
    }

    public record Fail(
        Long orderId,
        String transactionKey,
        String reason
    ) {
    }

}
