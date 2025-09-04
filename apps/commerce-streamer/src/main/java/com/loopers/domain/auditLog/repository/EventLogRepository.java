package com.loopers.domain.auditLog.repository;

import com.loopers.domain.auditLog.entity.EventLogEntity;

public interface EventLogRepository {

    EventLogEntity save(EventLogEntity auditLog);
}
