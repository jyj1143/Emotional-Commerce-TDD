package com.loopers.domain.payment.dto;

import com.loopers.domain.common.vo.Money;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.PaymentStatus;
import com.loopers.domain.payment.vo.CardNumber;

public record PaymentCommand() {

    public record Pay(
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
        Long orderId,
        Long paymentId,
        String transactionKey,
        PaymentStatus paymentStatus,
        Long amount,
        CardType cardType,
        String cardNumber
    ) {
    }

}
