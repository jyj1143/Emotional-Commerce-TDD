package com.loopers.domain.like.dto;

import com.loopers.domain.like.enums.LikeType;

public class LikeCommand {

    public record Like(
        Long userId,
        Long targetId,
        LikeType likeType
    ) {
    }

    public record Unlike(
        Long userId,
        Long targetId,
        LikeType likeType
    ) {
    }
}
