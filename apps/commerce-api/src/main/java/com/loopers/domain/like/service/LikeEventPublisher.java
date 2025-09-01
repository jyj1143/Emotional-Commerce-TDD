package com.loopers.domain.like.service;

import com.loopers.domain.like.dto.LikeEvent;

public interface LikeEventPublisher {

    void publish(LikeEvent.Like event);

    void publish(LikeEvent.UnLike event);

}
