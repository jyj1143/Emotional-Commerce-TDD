package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.application.payment.strategy.condition.PointPaymentCondition;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.service.PaymentService;
import com.loopers.domain.point.service.PointService;
import com.loopers.domain.point.service.dto.PointCommand.UsePoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointPaymentStrategy implements PaymentStrategy<PointPaymentCondition> {

    private final PaymentService paymentService;
    private final PointService pointService;

    @Override
    public PaymentResult pay(PointPaymentCondition condition) {
        // 포인트 사용
        pointService.usePoint(new UsePoint(condition.getUserId(), condition.getAmount()));
        // 결제 처리
        paymentService.pay(new PaymentCommand.Pay(condition.getUserId(), condition.getOrderId(), PaymentMethod.POINT, condition.getAmount()));
        return null;
    }
}
