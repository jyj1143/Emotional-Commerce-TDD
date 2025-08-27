package com.loopers.infrastructure.order;

import com.loopers.domain.order.dto.OrderEvent.Created;
import com.loopers.domain.order.service.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderApplicationEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(Created event) {
        publisher.publishEvent(event);
    }
}
