package com.loopers.application.payment.dto;

import com.loopers.domain.payment.adapter.PaymentGatewayCommand;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.enums.CardType;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.enums.PaymentStatus;

public record PaymentCriteria() {

    public record Pay(
        Long userId,
        Long orderId,
        PaymentMethod paymentMethod,
        Long amount
    ) {

        public PaymentCommand.Pay toPaymentCommand() {
            return new PaymentCommand.Pay(userId, orderId, paymentMethod, amount);
        }
    }

    public record PgPay(
        Long orderId,
        CardType cardType,
        String cardNo,
        Long amount
    ) {

        public PaymentGatewayCommand.Payment toPaymentCommand() {
            return new PaymentGatewayCommand.Payment(orderId, cardType, cardNo, amount);
        }
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
        public PaymentCommand.Synchronize toPaymentCommand(
        ) {
            return new PaymentCommand.Synchronize(transactionKey, orderId, cardType, cardNo, amount, status, reason);
        }
    }
}
