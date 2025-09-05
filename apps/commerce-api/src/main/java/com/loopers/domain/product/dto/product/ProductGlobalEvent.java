package com.loopers.domain.product.dto.product;

import java.time.ZonedDateTime;

public class ProductGlobalEvent {

    public record Viewed(
        Long productId,
        Long userId,
        ZonedDateTime createdAt
    ) {
        public static Viewed from(Long productId, Long userId) {
            return new Viewed(productId, userId, ZonedDateTime.now());
        }
    }

}
