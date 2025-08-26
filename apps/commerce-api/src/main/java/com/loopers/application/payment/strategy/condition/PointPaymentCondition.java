package com.loopers.application.payment.strategy.condition;

import lombok.Getter;

// 포인트 결제에 필요한 조건
@Getter
public class PointPaymentCondition extends PaymentCondition {

    private final Long userId;

    public PointPaymentCondition(Long orderId, Long amount, Long userId) {
        super(orderId, amount);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
