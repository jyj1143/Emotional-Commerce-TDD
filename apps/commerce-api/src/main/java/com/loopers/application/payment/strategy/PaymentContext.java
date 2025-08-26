package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.application.payment.strategy.condition.PaymentCondition;
import com.loopers.domain.payment.enums.PaymentMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 컨텍스트(전략 등록/실행)
 */
@Component
@RequiredArgsConstructor
public class PaymentContext {

    private final Map<PaymentMethod, PaymentStrategy<? extends PaymentCondition>> strategies = new HashMap<>();

    /**
     * 전략 등록 메소드
     */
    public void registerStrategy(PaymentMethod paymentMethod, PaymentStrategy<? extends PaymentCondition> strategy) {
        this.strategies.put(paymentMethod, strategy);
    }

    /**
     * PaymentMethod에 따라 적절한 전략을 선택하고 실행
     */
    public <T extends PaymentCondition> PaymentResult executePay(PaymentMethod paymentMethod, T condition) {
        PaymentStrategy<T> strategy = (PaymentStrategy<T>) strategies.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 결제 방법입니다: " + paymentMethod);
        }
        return strategy.pay(condition);
    }


}
