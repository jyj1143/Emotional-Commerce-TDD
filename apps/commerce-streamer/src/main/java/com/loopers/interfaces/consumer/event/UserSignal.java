package com.loopers.interfaces.consumer.event;

import java.time.ZonedDateTime;

public record UserSignal() {

    public record Liked(
        Long productId,
        Long userId,
        ZonedDateTime createdAt
    ) {
    }

    public record UnLiked(
        Long productId,
        Long userId,
        ZonedDateTime createdAt
    ) {
    }

    public record Viewed(
        Long productId,
        Long userId,
        ZonedDateTime createdAt
    ) {
    }

}
