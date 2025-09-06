package com.loopers.infrastructure.inventory;

import com.loopers.domain.inventory.dto.InventoryGlobalEvent;
import com.loopers.domain.inventory.service.InventoryGlobalV1EventPublisher;
import com.loopers.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryGlobalV1EventPublisherImpl implements InventoryGlobalV1EventPublisher {

    private final String VERSION = "1";

    @Value("${kafka.topics.product-stock}")
    private String productStockTopic;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(InventoryGlobalEvent.StockChanged event) {
        Message<InventoryGlobalEvent.StockChanged> message = Message.create(event, VERSION);
        String partitionKey = event.productSkuId().toString();
        kafkaTemplate.send(productStockTopic, partitionKey, message);
    }
}
