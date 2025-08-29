package com.loopers.infrastructure.like;

import com.loopers.domain.like.dto.LikeEvent;
import com.loopers.domain.like.service.LikeEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeApplicationEventPublisher implements LikeEventPublisher {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publish(LikeEvent.Like event) {
        log.info("Event published: type=[ProductLike], likeType=[{}], name=[{}]",
            event.targetId(), event.likeType());
        publisher.publishEvent(event);
    }

    @Override
    public void publish(LikeEvent.UnLike event) {
        log.info("Event published: type=[ProductUnLike], likeType=[{}], name=[{}]",
            event.targetId(), event.likeType());
        publisher.publishEvent(event);
    }
}
