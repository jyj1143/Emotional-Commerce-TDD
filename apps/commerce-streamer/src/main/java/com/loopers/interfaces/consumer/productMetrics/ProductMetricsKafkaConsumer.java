package com.loopers.interfaces.consumer.productMetrics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.domain.handledEvent.dto.HandledEventCommand;
import com.loopers.domain.handledEvent.service.HandledEventService;
import com.loopers.domain.product.ProductCacheService;
import com.loopers.domain.productMetrics.dto.ProductMetricsCommand;
import com.loopers.domain.productMetrics.dto.ProductMetricsEvent;
import com.loopers.domain.productMetrics.dto.ProductMetricsEvent.LikeList.Like;
import com.loopers.domain.productMetrics.dto.ProductMetricsEvent.OrderList.Order;
import com.loopers.domain.productMetrics.dto.ProductMetricsEvent.UnLikeList.UnLike;
import com.loopers.domain.productMetrics.dto.ProductMetricsEvent.ViewList.View;
import com.loopers.domain.productMetrics.service.ProductMetricsEventPublisher;
import com.loopers.domain.productMetrics.service.ProductMetricsService;
import com.loopers.interfaces.consumer.event.OrderEvent;
import com.loopers.interfaces.consumer.event.OrderEvent.Order.OrderItem;
import com.loopers.interfaces.consumer.event.UserSignal.Liked;
import com.loopers.interfaces.consumer.event.UserSignal.UnLiked;
import com.loopers.interfaces.consumer.event.UserSignal.Viewed;
import com.loopers.message.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductMetricsKafkaConsumer {

    private final HandledEventService handledEventService;
    private final ProductMetricsService productMetricsService;
    private final ProductCacheService productCacheService;
    private final ProductMetricsEventPublisher productMetricsEventPublisher;

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
    public void handleLikedEvent(
        @Payload List<Message<Liked>> messages,
        Acknowledgment acknowledgment
    ) {
        for (Message<Liked> message : messages) {

            // 이미 처리된 이벤트인지 확인
            handledEventService.save(new HandledEventCommand.Create(
                message.messageId(),
                CONSUMER_GROUP_METRICS,
                message.payload().toString(),
                message.publishedAt()
            ));

            // 좋아요 이벤트에 따른 상품 메트릭스 업데이트
            productMetricsService.increaseDailyLikeCount(
                new ProductMetricsCommand.IncreaseLikeCount(message.payload().productId()));

            // 상품의 캐시를 무효화합니다.
            productCacheService.invalidateCacheForZeroStock(message.payload().productId());
        }


        productMetricsEventPublisher.publish(
            new ProductMetricsEvent.LikeList(
                messages.stream().map(Message::payload).map(
                        item ->
                             Like.of(item.productId()))
                    .toList()
            )
        );
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
    public void handleUnLikedEvent(
        @Payload List<Message<UnLiked>> messages,
        Acknowledgment acknowledgment
    ) {
        for (Message<UnLiked> message : messages) {
            // 이미 처리된 이벤트인지 확인
            handledEventService.save(new HandledEventCommand.Create(
                message.messageId(),
                CONSUMER_GROUP_METRICS,
                message.payload().toString(),
                message.publishedAt()
            ));

            // 좋아요 취소 이벤트에 따른 상품 메트릭스 업데이트
            productMetricsService.decreaseDailyLikeCount(
                new ProductMetricsCommand.DecreaseLikeCount(message.payload().productId()));

            // 상품의 캐시를 무효화합니다.
            productCacheService.invalidateCacheForZeroStock(message.payload().productId());
        }

        productMetricsEventPublisher.publish(
            new ProductMetricsEvent.UnLikeList(
                messages.stream().map(Message::payload).map(
                        item ->
                             UnLike.of(item.productId()))
                    .toList()
            )
        );
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
    public void handleProductViewedEvent(
        @Payload List<Message<Viewed>> messages,
        Acknowledgment acknowledgment){
        for (Message<Viewed> message : messages) {
            // 이미 처리된 이벤트인지 확인
            handledEventService.save(new HandledEventCommand.Create(
                message.messageId(),
                CONSUMER_GROUP_METRICS,
                message.payload().toString(),
                message.publishedAt()
            ));

            // 상품 조회에 따른 상품 메트릭스 업데이트
            productMetricsService.increaseProductViewedCount(
                new ProductMetricsCommand.IncreaseProductViewedCount(message.payload().productId()));

        }

        productMetricsEventPublisher.publish(
            new ProductMetricsEvent.ViewList(
                messages.stream().map(Message::payload).map(
                        item ->
                            View.of(item.productId()))
                    .toList()
            )
        );
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
    public void handleOrderPaymentEvent(
        @Payload List<Message<OrderEvent.Order>> messages
        , Acknowledgment acknowledgment
    ) {
        for (Message<OrderEvent.Order> message : messages) {
            // 이미 처리된 이벤트인지 확인
            handledEventService.save(new HandledEventCommand.Create(
                message.messageId(),
                CONSUMER_GROUP_METRICS,
                message.payload().toString(),
                message.publishedAt()
            ));

            // 상품 조회에 따른 상품 메트릭스 업데이트
            message.payload().items().forEach(item -> {
                productMetricsService.increaseOrderPaymentCount(
                    new ProductMetricsCommand.IncreaseOrderPaymentCount(item.productId()));
            });
        }

        productMetricsEventPublisher.publish(
            new ProductMetricsEvent.OrderList(
                messages.stream()
                    .map(Message::payload)
                    .flatMap(order -> order.items().stream())
                    .map(OrderItem::productId)
                    .map(ProductMetricsEvent.OrderList.Order::of)
                    .toList()
            )
        );
        acknowledgment.acknowledge();
    }

}
