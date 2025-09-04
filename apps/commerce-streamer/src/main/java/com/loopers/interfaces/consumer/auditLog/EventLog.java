package com.loopers.interfaces.consumer.auditLog;

public record EventLog() {
    public record Audit(
            String eventId,
            String eventType,
            Long userId
    ) {
    }
}
