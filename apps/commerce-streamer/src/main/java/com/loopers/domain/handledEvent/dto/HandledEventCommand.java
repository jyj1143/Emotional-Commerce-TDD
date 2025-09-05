package com.loopers.domain.handledEvent.dto;

import com.loopers.domain.handledEvent.entity.HandledEvent;
import java.time.ZonedDateTime;

public record HandledEventCommand() {
    public record Create(
        String eventId,
        String groupId,
        String payload,
        ZonedDateTime publishedAt
    ) {
        public HandledEvent toEntity() {
            return HandledEvent.create(eventId, groupId, payload, publishedAt);
        }
    }
}
