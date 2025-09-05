package com.loopers.domain.handledEvent.service;

import com.loopers.domain.handledEvent.dto.HandledEventCommand;
import com.loopers.domain.handledEvent.entity.HandledEvent;
import com.loopers.domain.handledEvent.repository.HandledEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HandledEventService {
    private final HandledEventRepository handledEventRepository;

    @Transactional
    public HandledEvent save(HandledEventCommand.Create command) {
        try {
            return handledEventRepository.save(command.toEntity());
        } catch (DataIntegrityViolationException e) {
            return handledEventRepository.findByEventIdAndGroupId(command.eventId(), command.groupId())
                .orElseThrow(() -> e);
        }
    }
}
