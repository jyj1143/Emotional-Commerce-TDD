package com.loopers.infrastructure.auditLog;

import com.loopers.domain.auditLog.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogEntity, Long> {
}
