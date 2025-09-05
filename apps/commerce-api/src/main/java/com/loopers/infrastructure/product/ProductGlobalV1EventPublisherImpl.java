package com.loopers.infrastructure.product;

import com.loopers.domain.product.dto.product.ProductGlobalEvent;
import com.loopers.domain.product.service.ProductGlobalV1EventPublisher;
import com.loopers.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductGlobalV1EventPublisherImpl implements ProductGlobalV1EventPublisher {

    private final String VERSION = "1";

    @Value("${kafka.topics.product-viewed}")
    private String productViewTopic;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(ProductGlobalEvent.Viewed event) {
        Message<ProductGlobalEvent.Viewed> message = Message.create(event, VERSION);
        String partitionKey = event.productId().toString();
        kafkaTemplate.send(productViewTopic, partitionKey, message);
    }

}
