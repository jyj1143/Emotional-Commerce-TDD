package com.loopers.domain.like.dto;

import java.time.ZonedDateTime;

public class LikeGlobalEvent {

    public record Liked(
        Long productId,
        Long userId,
        ZonedDateTime createdAt
    ) {
        public static Liked from(Long productId, Long userId) {
            return new Liked(productId, userId, ZonedDateTime.now());
        }
    }

    public record UnLiked(
        Long productId,
        Long userId,
        ZonedDateTime createdAt
    ) {
    }
}
