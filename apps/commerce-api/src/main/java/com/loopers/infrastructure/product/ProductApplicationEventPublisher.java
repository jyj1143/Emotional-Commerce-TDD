package com.loopers.infrastructure.product;

import com.loopers.domain.product.dto.product.ProductEvent;
import com.loopers.domain.product.service.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductApplicationEventPublisher implements ProductEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(ProductEvent.Register event) {
        publisher.publishEvent(event);
    }
}
