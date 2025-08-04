package com.loopers.application.order.dto;

import com.loopers.domain.order.OrderItemModel;
import java.util.List;

public record OrderCriteria() {

    public record Order(
        Long userId,
        List<OrderItemModel> items
    ) {
    }

}
