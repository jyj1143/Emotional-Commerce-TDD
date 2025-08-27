package com.loopers.infrastructure.like;

import com.loopers.domain.like.dto.LikeEvent;
import com.loopers.domain.like.service.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeApplicationEventPublisher implements LikeEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(LikeEvent.Like event) {
        publisher.publishEvent(event);
    }

    @Override
    public void publish(LikeEvent.UnLike event) {
        publisher.publishEvent(event);
    }
}
