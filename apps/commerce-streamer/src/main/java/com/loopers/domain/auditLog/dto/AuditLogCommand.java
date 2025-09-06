package com.loopers.domain.auditLog.dto;

import com.loopers.domain.auditLog.entity.EventLogEntity;
import java.time.ZonedDateTime;

public record AuditLogCommand() {

    public record Create(
        String eventId,
        String eventType,
        String topic,
        String groupId,
        String partitionKey,
        String payload,
        ZonedDateTime publishedAt,
        String version
    ) {
        public EventLogEntity toEntity() {
            return EventLogEntity.create(eventId, eventType, topic, groupId, partitionKey, payload, publishedAt, version);
        }
    }
}
