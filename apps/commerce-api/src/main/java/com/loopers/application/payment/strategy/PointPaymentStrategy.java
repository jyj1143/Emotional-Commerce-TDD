package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.service.PaymentService;
import com.loopers.domain.point.service.PointService;
import com.loopers.domain.point.service.dto.PointCommand.UsePoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointPaymentStrategy implements PaymentStrategy {

    private final PaymentService paymentService;
    private final PointService pointService;

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.POINT;
    }

    @Override
    public PaymentResult pay(PaymentCriteria.Pay criteria) {
        // 포인트 사용
        pointService.usePoint(new UsePoint(criteria.userId(), criteria.amount()));
        // 결제 처리
        paymentService.pay(criteria.toPaymentCommand());
        return null;
    }

}
