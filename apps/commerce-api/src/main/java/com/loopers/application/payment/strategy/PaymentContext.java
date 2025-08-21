package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.application.payment.strategy.condition.PaymentCondition;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 컨텍스트(전략 등록/실행)
 */
@Component
@RequiredArgsConstructor
public class PaymentContext {

    private PaymentStrategy<? extends PaymentCondition> strategy;

    /**
     * 전략 교체 메소드
     */
    public void registerStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 전략 실행 메소드
     * @param condition 결제 조건
     * @return 결제 결과
     * @param <T> 결제 조건 타입
     */
    public <T extends PaymentCondition> PaymentResult executePay(T condition) {
        if (strategy == null) {
            throw new IllegalStateException("결제 전략이 설정되지 않았습니다.");
        }
        return ((PaymentStrategy<T>) strategy).pay(condition);
    }

}
