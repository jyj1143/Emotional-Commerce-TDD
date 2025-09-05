package com.loopers.domain.order.dto;

import com.loopers.domain.order.OrderModel;
import java.time.ZonedDateTime;
import java.util.List;

public record OrderGlobalEvent () {
    public record Order(
        Long orderId,
        Long userId,
        List<OrderItem> items,
        ZonedDateTime createdAt
    ){
        public record OrderItem(
            Long productId,
            Long quantity
        ) {
        }

        public static Order from(OrderModel order){
            return new Order(
                order.getId(),
                order.getRefUserId(),
                order.getOrderItemModels().stream()
                    .map(item -> new OrderItem(
                        item.getRefProductSkuId(),
                        item.getQuantity().getQuantity()
                    )).toList(),
                ZonedDateTime.now()
            );
        }

    }
}
