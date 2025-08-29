package com.loopers.domain.order.dto;

import com.loopers.domain.order.OrderModel;
import java.util.List;

public record OrderEvent() {
    public record Created(
        Long orderId,
        Long userId,
        Long couponId,
        List<OrderItem> items,
        Long finalPrice
    ) {
        public static OrderEvent.Created from(OrderModel order, Long couponId) {
            return new OrderEvent.Created(
                order.getId(),
                order.getRefUserId(),
                couponId,
                order.getOrderItemModels().stream()
                    .map(item -> new OrderItem(
                        item.getId(),
                        item.getOrder().getId(),
                        item.getQuantity().getQuantity(),
                        item.getPurchasePrice().getAmount(),
                        item.getRefProductSkuId()
                        )
                    ).toList(),
                order.getTotalPrice().getAmount()
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
