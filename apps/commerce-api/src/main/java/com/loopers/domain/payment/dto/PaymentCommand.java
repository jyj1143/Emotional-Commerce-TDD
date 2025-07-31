package com.loopers.domain.payment.dto;

import com.loopers.domain.payment.enums.PaymentMethod;

public record PaymentCommand() {

    public record Pay(
        Long orderId,
        PaymentMethod method,
        Long amount
    ){
    }

}
