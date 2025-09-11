package com.loopers.interfaces.consumer.product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.domain.product.ProductCacheService;
import com.loopers.interfaces.event.ProductEvent.StockChanged;
import com.loopers.interfaces.event.UserSignal.Liked;
import com.loopers.message.Message;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaConsumer {

    private final ProductCacheService productCacheService;
    private final ObjectMapper objectMapper;

    /**
     * 재고 변경 이벤트 처리 - 재고가 0인 경우 캐시 무효화
     */
    @KafkaListener(
        topics = "${kafka.consumers.topics.product-stock}",
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "${kafka.consumers.groups.product}"
    )
    public void handleProductStockChanged(
        @Payload List<Message<StockChanged>> messages,
        Acknowledgment acknowledgment
    ) {
        try {
            for (Message<StockChanged> message : messages) {

                if (message.payload().quantity() == 0) {
                    Long productSkuId = message.payload().productSkuId();
                    productCacheService.invalidateCacheForZeroStock(productSkuId);
                }
            }
        } catch (Exception e) {
            log.error("재고 변경 메시지 처리 중 오류 발생", e);
        }
        acknowledgment.acknowledge();
    }
}

