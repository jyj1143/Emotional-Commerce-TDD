package com.loopers.domain.auditLog.repository;

import com.loopers.domain.auditLog.entity.AuditLogEntity;

public interface AuditLogRepository {

    AuditLogEntity save(AuditLogEntity auditLog);
}
