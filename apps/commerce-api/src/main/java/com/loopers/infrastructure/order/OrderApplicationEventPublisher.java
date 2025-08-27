package com.loopers.infrastructure.order;

import com.loopers.domain.order.dto.OrderEvent.Created;
import com.loopers.domain.order.service.OrderEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderApplicationEventPublisher implements OrderEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(Created event) {
        log.info("Event published: type=[OrderCreated], orderId=[{}], userId=[{}], itemCount=[{}]",
            event.orderId(), event.userId(),  event.items().size());
        publisher.publishEvent(event);
    }
}
