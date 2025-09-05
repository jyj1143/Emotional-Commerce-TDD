package com.loopers.domain.handledEvent.repository;

import com.loopers.domain.handledEvent.entity.HandledEvent;
import java.util.Optional;

public interface HandledEventRepository {

    HandledEvent save(HandledEvent handledEvent);

    Optional<HandledEvent> findByEventIdAndGroupId(String eventId, String groupId);
}
