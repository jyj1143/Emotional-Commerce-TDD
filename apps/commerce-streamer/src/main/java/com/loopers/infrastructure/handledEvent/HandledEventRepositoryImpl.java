package com.loopers.infrastructure.handledEvent;

import com.loopers.domain.handledEvent.entity.HandledEvent;
import com.loopers.domain.handledEvent.repository.HandledEventRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HandledEventRepositoryImpl implements HandledEventRepository {
    private final HandledEventJpaRepository handledEventJpaRepository;

    @Override
    public HandledEvent save(HandledEvent handledEvent) {
        return handledEventJpaRepository.save(handledEvent);
    }

    public Optional<HandledEvent> findByEventIdAndGroupId(String eventId, String groupId) {
        return handledEventJpaRepository.findByEventIdAndGroupId(eventId, groupId);
    }
}
