package com.loopers.interfaces.consumer.event;

import java.time.ZonedDateTime;
import java.util.List;

public record OrderEvent() {

    public record Order(
        Long orderId,
        Long userId,
        List<OrderItem> items,
        ZonedDateTime createdAt
    ){
        public record OrderItem(
            Long productId,
            Long quantity,
            Long productSkuId
        ) {
        }
    }

}
