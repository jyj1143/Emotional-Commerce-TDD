package com.loopers.infrastructure.auditLog;

import com.loopers.domain.auditLog.entity.EventLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogJpaRepository extends JpaRepository<EventLogEntity, Long> {
}
