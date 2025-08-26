package com.loopers.application.payment.strategy.condition;

import lombok.Getter;

// 공통 결제 조건
@Getter
public abstract class PaymentCondition {

    private final Long orderId;
    private final Long amount;

    protected PaymentCondition(Long orderId, Long amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
}
