package com.loopers.domain.order.dto;

import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.enums.OrderStatus;

public record OrderInfo(
    Long id,
    Long userId,
    OrderStatus status,
    Long totalPrice
) {
    public static OrderInfo from(final OrderModel order) {
        return new OrderInfo(
            order.getId(),
            order.getRefUserId(),
            order.getStatus(),
            order.calculateTotalPrice()
        );
    }
}
