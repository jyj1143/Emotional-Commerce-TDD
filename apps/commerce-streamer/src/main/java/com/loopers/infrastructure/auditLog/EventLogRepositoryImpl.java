package com.loopers.infrastructure.auditLog;

import com.loopers.domain.auditLog.entity.EventLogEntity;
import com.loopers.domain.auditLog.repository.EventLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EventLogRepositoryImpl implements EventLogRepository {
    private final EventLogJpaRepository eventLogJpaRepository;

    @Override
    public EventLogEntity save(EventLogEntity auditLog) {
        return eventLogJpaRepository.save(auditLog);
    }
}
