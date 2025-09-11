package com.loopers.domain.productMetrics.service;

import com.loopers.domain.productMetrics.dto.ProductMetricsEvent;

public interface ProductMetricsEventPublisher {

    void publish(ProductMetricsEvent.LikeList event);

    void publish(ProductMetricsEvent.UnLikeList event);

    void publish(ProductMetricsEvent.ViewList event);

    void publish(ProductMetricsEvent.OrderList event);
}
