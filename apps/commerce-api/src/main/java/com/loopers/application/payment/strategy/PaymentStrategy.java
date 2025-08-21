package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.domain.payment.enums.PaymentMethod;

public interface PaymentStrategy {

    boolean supports(PaymentMethod paymentMethod);

    PaymentResult pay(PaymentCriteria.Pay criteria);
}
