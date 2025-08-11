package com.loopers.domain.order.dto;

import com.loopers.domain.order.OrderItemModel;
import java.util.List;

public record OrderCommand() {

    public record Order(
        Long userId,
        List<OrderItem> orderItem
    ) {
        public record OrderItem(
            Long quantity,
            Long purchasePrice,
            Long refProductSkuId
        ) {
            public OrderItemModel toOrderItem() {
                return OrderItemModel.of(quantity, purchasePrice, refProductSkuId);
            }
        }
    }


}
