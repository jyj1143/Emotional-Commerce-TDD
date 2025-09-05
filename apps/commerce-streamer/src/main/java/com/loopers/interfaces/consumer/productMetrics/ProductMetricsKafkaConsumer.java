package com.loopers.interfaces.consumer.productMetrics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.domain.handledEvent.dto.HandledEventCommand;
import com.loopers.domain.handledEvent.service.HandledEventService;
import com.loopers.domain.productMetrics.dto.ProductMetricsCommand;
import com.loopers.domain.productMetrics.service.ProductMetricsService;
import com.loopers.interfaces.event.UserSignal.Liked;
import com.loopers.message.Message;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMetricsKafkaConsumer {

    private final HandledEventService handledEventService;
    private final ProductMetricsService productMetricsService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.consumers.groups.product-metrics}")
    private String CONSUMER_GROUP_METRICS;

    /**
     * 상품 좋아요 이벤트 처리
     */
    @KafkaListener(
        topics = "${kafka.consumers.topics.liked}",
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "${kafka.consumers.groups.product-metrics}"
    )
    public void handleLikedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
        throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            Message<Liked> event = objectMapper.readValue(message.value(), new TypeReference<>() {
            });
            // 이미 처리된 이벤트인지 확인
            handledEventService.save(new HandledEventCommand.Create(
                event.messageId(),
                CONSUMER_GROUP_METRICS,
                event.payload().toString(),
                event.publishedAt()
            ));

            // 좋아요 이벤트에 따른 상품 메트릭스 업데이트
            productMetricsService.increaseDailyLikeCount(
                new ProductMetricsCommand.IncreaseLikeCount(event.payload().productId()));

        }
        acknowledgment.acknowledge();
    }

    /**
     * 상품 좋아요 취소 이벤트 처리
     */
    @KafkaListener(
        topics = "${kafka.consumers.topics.un-liked}",
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "${kafka.consumers.groups.product-metrics}"
    )
    public void handleUnLikedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
        throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            Message<Liked> event = objectMapper.readValue(message.value(), new TypeReference<>() {
            });
            // 이미 처리된 이벤트인지 확인
            handledEventService.save(new HandledEventCommand.Create(
                event.messageId(),
                CONSUMER_GROUP_METRICS,
                event.payload().toString(),
                event.publishedAt()
            ));

            // 좋아요 취소 이벤트에 따른 상품 메트릭스 업데이트
            productMetricsService.decreaseDailyLikeCount(
                new ProductMetricsCommand.DecreaseLikeCount(event.payload().productId()));

        }
        acknowledgment.acknowledge();
    }

    /**
     * 상품 조회 이벤트 처리
     */
    @KafkaListener(
        topics = "${kafka.consumers.topics.product-viewed}",
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "${kafka.consumers.groups.product-metrics}"
    )
    public void handleProductViewedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
        throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            Message<Liked> event = objectMapper.readValue(message.value(), new TypeReference<>() {
            });
            // 이미 처리된 이벤트인지 확인
            handledEventService.save(new HandledEventCommand.Create(
                event.messageId(),
                CONSUMER_GROUP_METRICS,
                event.payload().toString(),
                event.publishedAt()
            ));

            // 상품 조회에 따른 상품 메트릭스 업데이트
            productMetricsService.increaseProductViewedCount(
                new ProductMetricsCommand.IncreaseProductViewedCount(event.payload().productId()));

        }
        acknowledgment.acknowledge();
    }

    /**
     * 주문 생성 이벤트 처리
     */
    @KafkaListener(
        topics = "${kafka.consumers.topics.order-payment}",
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "${kafka.consumers.groups.product-metrics}"
    )
    public void handleOrderPaymentEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment)
        throws IOException {
        for (ConsumerRecord<String, byte[]> message : messages) {
            Message<Liked> event = objectMapper.readValue(message.value(), new TypeReference<>() {
            });
            // 이미 처리된 이벤트인지 확인
            handledEventService.save(new HandledEventCommand.Create(
                event.messageId(),
                CONSUMER_GROUP_METRICS,
                event.payload().toString(),
                event.publishedAt()
            ));

            // 상품 조회에 따른 상품 메트릭스 업데이트
            productMetricsService.increaseOrderPaymentCount(
                new ProductMetricsCommand.IncreaseOrderPaymentCount(event.payload().productId()));

        }
        acknowledgment.acknowledge();
    }

}
