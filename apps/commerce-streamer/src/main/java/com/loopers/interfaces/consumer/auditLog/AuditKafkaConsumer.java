package com.loopers.interfaces.consumer.auditLog;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.domain.auditLog.dto.AuditLogCommand;
import com.loopers.domain.auditLog.service.AuditLogService;
import com.loopers.interfaces.consumer.event.OrderEvent;
import com.loopers.interfaces.consumer.event.UserSignal;
import com.loopers.message.Message;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditKafkaConsumer {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Value("${kafka.consumers.groups.audit-log}")
    private String CONSUMER_GROUP_AUDIT_LOG;

    @Value("${kafka.consumers.topics.liked}")
    private String likedTopic;

    @Value("${kafka.consumers.topics.un-liked}")
    private String unLikedTopic;

    @Value("${kafka.consumers.topics.product-viewed}")
    private String productViewedTopic;

    @Value("${kafka.consumers.topics.order-payment}")
    private String orderPaymentTopic;

    private final Map<String, String> topicToPayloadTypeMap = new HashMap<>();

    @PostConstruct
    public void init() {
        // 토픽과 해당 이벤트 타입 매핑
        topicToPayloadTypeMap.put(likedTopic, UserSignal.Liked.class.toString());
        topicToPayloadTypeMap.put(unLikedTopic, UserSignal.UnLiked.class.toString());
        topicToPayloadTypeMap.put(productViewedTopic, UserSignal.Viewed.class.toString());
        topicToPayloadTypeMap.put(orderPaymentTopic, OrderEvent.Order.class.toString());
    }

    @KafkaListener(
        topics = {
            "${kafka.consumers.topics.liked}",
            "${kafka.consumers.topics.un-liked}",
            "${kafka.consumers.topics.product-viewed}",
            "${kafka.consumers.topics.order-payment}"
        },
        containerFactory = KafkaConfig.BATCH_LISTENER,
        groupId = "${kafka.consumers.groups.audit-log}"
    )
    public void handleAllAuditEvents(
        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
        @Header(KafkaHeaders.PARTITION) String partition,
        @Payload List<Message<?>> messages,  // 제네릭 타입으로 메시지 디코딩
        Acknowledgment acknowledgment){

        for (Message<?> message : messages) {
            log.debug("메시지 수신: 토픽={}", topic);
            try {
                auditLogService.save(new AuditLogCommand.Create(
                    message.messageId(),
                    topicToPayloadTypeMap.getOrDefault(topic, "UnknownEventClass"),
                    topic,
                    CONSUMER_GROUP_AUDIT_LOG,
                    String.valueOf(partition),
                    message.payload().toString(),
                    message.publishedAt(),
                    message.version()
                ));
            } catch (Exception e) {
                log.error("메시지 처리 중 오류 발생: 토픽={}, 오류={}", topic, e.getMessage(), e);
            }
        }
        acknowledgment.acknowledge();
    }

}
