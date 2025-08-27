package com.loopers.infrastructure.product;

import com.loopers.domain.product.dto.product.ProductEvent;
import com.loopers.domain.product.service.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductApplicationEventPublisher implements ProductEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(ProductEvent.Register event) {
        log.info("Event published: type=[ProductRegistered], productId=[{}], name=[{}], price=[{}]",
            event.productId(), event.name(), event.price());
        publisher.publishEvent(event);
    }
}
