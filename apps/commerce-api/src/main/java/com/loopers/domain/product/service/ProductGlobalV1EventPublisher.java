package com.loopers.domain.product.service;

import com.loopers.domain.product.dto.product.ProductGlobalEvent;

public interface ProductGlobalV1EventPublisher {

    void publish(ProductGlobalEvent.Viewed event);
}
