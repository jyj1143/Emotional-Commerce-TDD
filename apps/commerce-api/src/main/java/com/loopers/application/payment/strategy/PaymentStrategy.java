package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.application.payment.strategy.condition.PaymentCondition;

public interface PaymentStrategy<T extends PaymentCondition> {
    PaymentResult pay(T criteria);
}
