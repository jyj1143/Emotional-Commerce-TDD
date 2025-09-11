package com.loopers.infrastructure.productMetrics;

import com.loopers.domain.productMetrics.dto.ProductMetricsEvent;
import com.loopers.domain.productMetrics.service.ProductMetricsEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public abstract class ProductMetricsApplicationEventPublisher implements ProductMetricsEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(ProductMetricsEvent.LikeList event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publish(ProductMetricsEvent.UnLikeList event) {
        publisher.publishEvent(event);

    }

    @Override
    public void publish(ProductMetricsEvent.ViewList event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publish(ProductMetricsEvent.OrderList event) {
        publisher.publishEvent(event);
    }

}
