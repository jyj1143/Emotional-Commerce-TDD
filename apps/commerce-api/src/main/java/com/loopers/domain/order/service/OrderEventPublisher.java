package com.loopers.domain.order.service;

import com.loopers.domain.order.dto.OrderEvent;

public interface OrderEventPublisher {

    void publish(OrderEvent.Created event);

    void publish(OrderEvent.Cancelled event);
}
