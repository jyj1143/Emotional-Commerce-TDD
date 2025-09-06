package com.loopers.infrastructure.order;

import com.loopers.domain.inventory.dto.InventoryGlobalEvent;
import com.loopers.domain.inventory.dto.InventoryGlobalEvent.StockChanged;
import com.loopers.domain.order.dto.OrderGlobalEvent;
import com.loopers.domain.order.service.OrderGlobalV1EventPublisher;
import com.loopers.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderGlobalV1EventPublisherImpl implements OrderGlobalV1EventPublisher {
    private final String VERSION = "1";

    @Value("${kafka.topics.order-payment}")
    private String orderPaymentTopic;

    private final KafkaTemplate<Object, Object> kafkaTemplate;

    @Override
    public void publish(OrderGlobalEvent.Order event) {
        Message<OrderGlobalEvent.Order> message = Message.create(event, VERSION);
        String partitionKey = event.orderId().toString();
        kafkaTemplate.send(orderPaymentTopic, partitionKey, message);
    }
}
