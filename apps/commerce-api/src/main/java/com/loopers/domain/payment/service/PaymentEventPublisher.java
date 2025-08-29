package com.loopers.domain.payment.service;

import com.loopers.domain.payment.dto.PaymentEvent;

public interface PaymentEventPublisher {

    void publish(PaymentEvent.PaymentSucceeded event);

    void publish(PaymentEvent.PaymentFailed event);
}

