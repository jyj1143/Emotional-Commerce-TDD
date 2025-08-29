package com.loopers.domain.payment.dto;


import com.loopers.domain.payment.entity.PaymentGatewayTransactionModel;
import com.loopers.domain.payment.entity.PaymentModel;

public record PaymentEvent() {

    public record PaymentSucceeded(
        Long paymentId,
        Long orderId,
        Long userId,
        String transactionKey,
        Long amount
    ) {
        public static PaymentSucceeded from(PaymentModel payment, PaymentGatewayTransactionModel transaction) {
            return new PaymentSucceeded(
                payment.getId(),
                payment.getRefOrderId(),
                payment.getRefUserId(),
                transaction.getTransactionKey(),
                payment.getAmount().getAmount()
            );
        }
    }

    public record PaymentFailed(
        Long paymentId,
        Long orderId,
        Long userId,
        String transactionKey,
        Long amount,
        String reason
    ) {
        public static PaymentFailed from(PaymentModel payment, PaymentGatewayTransactionModel transaction) {
            return new PaymentFailed(
                payment.getId(),
                payment.getRefOrderId(),
                payment.getRefUserId(),
                transaction.getTransactionKey(),
                payment.getAmount().getAmount(),
                transaction.getReason()
            );
        }
    }
}
