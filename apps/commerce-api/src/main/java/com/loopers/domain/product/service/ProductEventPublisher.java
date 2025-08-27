package com.loopers.domain.product.service;

import com.loopers.domain.product.dto.product.ProductEvent;

public interface ProductEventPublisher {

    void publish(ProductEvent.Register event);

}
