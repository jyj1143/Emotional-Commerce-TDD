package com.loopers.domain.like.dto;

import com.loopers.domain.like.enums.LikeType;

public record LikeEvent() {

    public record Like(
        Long targetId,
        LikeType likeType
    ) {
        public static Like of(Long targetId, LikeType likeType) {
            return new Like(targetId, likeType);
        }
    }

    public record UnLike(
        Long targetId,
        LikeType likeType
    ) {
        public static UnLike of(Long targetId, LikeType likeType) {
            return new UnLike(targetId, likeType);
        }
    }

}
