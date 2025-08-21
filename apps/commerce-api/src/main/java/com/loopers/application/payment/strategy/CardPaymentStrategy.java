package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentCriteria;
import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardPaymentStrategy implements PaymentStrategy {

    private final PaymentService paymentService;

    @Override
    public boolean supports(PaymentMethod paymentMethod) {
        return paymentMethod == PaymentMethod.CARD;
    }

    @Override
    public PaymentResult pay(PaymentCriteria.Pay criteria) {
        // 결제 준비
        paymentService.ready(criteria.toPaymentCommand());
        return null;
    }
}
