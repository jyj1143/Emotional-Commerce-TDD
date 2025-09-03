package com.loopers.domain.auditLog.dto;

import com.loopers.domain.auditLog.entity.AuditLogEntity;
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
        public AuditLogEntity toEntity() {
            return AuditLogEntity.create(eventId, eventType, topic, groupId, partitionKey, payload, publishedAt, version);
        }
    }
}
