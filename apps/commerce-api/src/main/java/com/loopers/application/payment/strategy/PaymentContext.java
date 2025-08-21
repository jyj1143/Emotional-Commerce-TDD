package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentContext {

    private final List<PaymentStrategy> strategies;

    public PaymentResult pay(PaymentCriteria.Pay criteria) {
        PaymentStrategy strategy = strategies.stream()
            .filter(s -> s.supports(criteria.paymentMethod()))
            .findFirst()
            .orElseThrow(() -> new CoreException(ErrorType.CONFLICT, "지원하지 않는 결제수단입니다. :" + criteria.paymentMethod().name()));

        return strategy.pay(criteria);
    }
}
