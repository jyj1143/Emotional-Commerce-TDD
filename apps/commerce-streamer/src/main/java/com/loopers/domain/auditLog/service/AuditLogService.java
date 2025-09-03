package com.loopers.domain.auditLog.service;

import com.loopers.domain.auditLog.dto.AuditLogCommand;
import com.loopers.domain.auditLog.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public void save(AuditLogCommand.Create command) {
        auditLogRepository.save(command.toEntity());
    }
}
