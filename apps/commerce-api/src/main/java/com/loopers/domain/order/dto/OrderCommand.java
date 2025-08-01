package com.loopers.domain.order.dto;

import com.loopers.domain.order.OrderItemModel;
import java.util.List;

public record OrderCommand() {

    public record Order(
        Long userId,
        List<OrderItemModel> orderItemModels
    ) {

    }
}
