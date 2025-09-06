package com.loopers.domain.order.service;

import com.loopers.domain.order.dto.OrderGlobalEvent;

public interface OrderGlobalV1EventPublisher {

    void publish(OrderGlobalEvent.Order event);
}
