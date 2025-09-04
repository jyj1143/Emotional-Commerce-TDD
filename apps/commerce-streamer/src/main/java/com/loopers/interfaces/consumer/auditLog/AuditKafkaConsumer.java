package com.loopers.interfaces.consumer.auditLog;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.domain.auditLog.dto.AuditLogCommand;
import com.loopers.domain.auditLog.service.AuditLogService;
import com.loopers.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditKafkaConsumer {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @KafkaListener(
            topics = "${kafka.consumers.audit-log.source-topic}",
            containerFactory = KafkaConfig.BATCH_LISTENER,
            groupId = "${kafka.consumers.audit-log.group-id}"
    )
    public void consumeLikedEvent(List<ConsumerRecord<String, byte[]>> messages, Acknowledgment acknowledgment) throws IOException {

        for (ConsumerRecord<String, byte[]> message : messages) {
            Message<EventLog.Audit> event  = objectMapper.readValue(message.value(), new TypeReference<>() {});

            AuditLogCommand.Create command;
            auditLogService.save(command);
        }
        acknowledgment.acknowledge();
    }
}
