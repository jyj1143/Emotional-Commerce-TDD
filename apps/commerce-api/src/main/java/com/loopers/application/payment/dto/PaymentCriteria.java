package com.loopers.application.payment.dto;

import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.enums.PaymentMethod;

public record PaymentCriteria() {
    public record Pay(
        Long userId,
        Long orderId,
        PaymentMethod paymentMethod,
        Long amount
    ){
        public PaymentCommand.Pay toPaymentCommand() {
            return new PaymentCommand.Pay(orderId, paymentMethod, amount);
        }
    }
}
