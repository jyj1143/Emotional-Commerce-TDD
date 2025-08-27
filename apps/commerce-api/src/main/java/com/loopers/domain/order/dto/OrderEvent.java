package com.loopers.domain.order.dto;

import com.loopers.domain.order.OrderModel;
import java.util.List;

public record OrderEvent() {
    public record Created(
        Long orderId,
        Long userId,
        List<OrderItem> items
    ) {
        public static OrderEvent.Created from(OrderModel order) {
            return new OrderEvent.Created(
                order.getId(),
                order.getRefUserId(),
                order.getOrderItemModels().stream()
                    .map(item -> new OrderItem(
                        item.getId(),
                        item.getOrder().getId(),
                        item.getQuantity().getQuantity(),
                        item.getPurchasePrice().getAmount(),
                        item.getRefProductSkuId()
                        )
                    ).toList()
            );
        }
        public record OrderItem(
            Long id,
            Long orderId,
            Long quantity,
            Long purchasePrice,
            Long productSkuId
        ) {
        }
    }
}
