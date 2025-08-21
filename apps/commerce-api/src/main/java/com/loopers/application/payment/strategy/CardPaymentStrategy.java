package com.loopers.application.payment.strategy;

import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.application.payment.strategy.condition.CardPaymentCondition;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.enums.PaymentMethod;
import com.loopers.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardPaymentStrategy implements PaymentStrategy<CardPaymentCondition> {

    private final PaymentService paymentService;

    @Override
    public PaymentResult pay(CardPaymentCondition condition) {
        // 결제 준비
        paymentService.ready(new PaymentCommand.Pay(condition.getOrderId(), PaymentMethod.CARD, condition.getAmount()));
        return null;
    }
}

