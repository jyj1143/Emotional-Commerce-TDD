package com.loopers.message;

import java.time.ZonedDateTime;
import java.util.UUID;

public record Message<T>(
    String messageId, // UUID, 메시지 식별할 수 있는 KEY
    String version,
    ZonedDateTime publishedAt,
    T payload
) {

    public static <T> Message<T> create(T payload, String version) {
        return new Message<>(
            UUID.randomUUID().toString(),
            version,
            ZonedDateTime.now(),
            payload
        );
    }
}
