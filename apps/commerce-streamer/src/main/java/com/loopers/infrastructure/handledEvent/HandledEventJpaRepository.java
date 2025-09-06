package com.loopers.infrastructure.handledEvent;

import com.loopers.domain.handledEvent.entity.HandledEvent;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HandledEventJpaRepository extends JpaRepository<HandledEvent, Long> {

    Optional<HandledEvent> findByEventIdAndGroupId(String eventId, String groupId);

}
