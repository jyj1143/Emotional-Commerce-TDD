package com.loopers.domain.like.service;

import com.loopers.domain.like.dto.LikeGlobalEvent;

public interface LikeGlobalV1EventPublisher {

    void publish(LikeGlobalEvent.Liked event);

    void publish(LikeGlobalEvent.UnLiked event);
}
