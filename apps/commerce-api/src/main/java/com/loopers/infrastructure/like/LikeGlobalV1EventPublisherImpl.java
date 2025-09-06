package com.loopers.infrastructure.like;

import com.loopers.domain.like.dto.LikeGlobalEvent;
import com.loopers.domain.like.service.LikeGlobalV1EventPublisher;
import com.loopers.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeGlobalV1EventPublisherImpl implements LikeGlobalV1EventPublisher {

    private final String VERSION = "1";

    @Value("${kafka.topics.liked}")
    private String likeTopic;

    @Value("${kafka.topics.un-liked}")
    private String unLikeTopic;

    private final KafkaTemplate<Object, Object> kafkaTemplate;


    @Override
    public void publish(LikeGlobalEvent.Liked event) {
        Message<LikeGlobalEvent.Liked> message = Message.create(event, VERSION);
        String partitionKey = event.productId().toString();
        kafkaTemplate.send(likeTopic, partitionKey, message);
    }

    @Override
    public void publish(LikeGlobalEvent.UnLiked event) {
        Message<LikeGlobalEvent.UnLiked> message = Message.create(event, VERSION);
        String partitionKey = event.productId().toString();
        kafkaTemplate.send(unLikeTopic, partitionKey, message);
    }
}
