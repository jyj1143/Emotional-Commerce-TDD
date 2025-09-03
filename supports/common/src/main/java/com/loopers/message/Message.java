package com.loopers.message;

import java.time.ZonedDateTime;

public record Message<T>(
    String messageId, // UUID, 메시지 식별할 수 있는 KEY
    String version,
    ZonedDateTime publishedAt,
    T payload
) {

}
