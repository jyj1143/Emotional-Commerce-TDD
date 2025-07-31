package com.loopers.application.order.dto;

import com.loopers.domain.order.dto.OrderInfo;
import com.loopers.domain.order.enums.OrderStatus;

public record OrderResult(
    Long id,
    Long userId,
    OrderStatus status,
    Long totalPrice
) {

    public static OrderResult from(OrderInfo order) {
        return new OrderResult(
            order.id(),
            order.userId(),
            order.status(),
            order.totalPrice()
        );
    }

}
