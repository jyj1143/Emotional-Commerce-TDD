package com.loopers.infrastructure.auditLog;

import com.loopers.domain.auditLog.entity.AuditLogEntity;
import com.loopers.domain.auditLog.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {
    private final AuditLogJpaRepository auditLogJpaRepository;

    @Override
    public AuditLogEntity save(AuditLogEntity auditLog) {
        return auditLogJpaRepository.save(auditLog);
    }
}
