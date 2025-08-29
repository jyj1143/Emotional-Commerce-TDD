package com.loopers.infrastructure.payment;

import com.loopers.domain.payment.dto.PaymentEvent.PaymentFailed;
import com.loopers.domain.payment.dto.PaymentEvent.PaymentSucceeded;
import com.loopers.domain.payment.service.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentApplicationEventPublisher implements PaymentEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(PaymentSucceeded event) {
        log.info("Event published: type=[PaymentSucceeded], orderId=[{}], userId=[{}], paymentId=[{}]",
            event.orderId(), event.userId(), event.paymentId());
        publisher.publishEvent(event);
    }

    @Override
    public void publish(PaymentFailed event) {
        log.info("Event published: type=[PaymentFailed], orderId=[{}], userId=[{}], paymentId=[{}]",
            event.orderId(), event.userId(), event.paymentId());
        publisher.publishEvent(event);
    }
}
