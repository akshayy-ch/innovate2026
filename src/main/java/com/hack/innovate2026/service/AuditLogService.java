package com.hack.innovate2026.service;

import com.hack.innovate2026.entity.AuditLog;
import com.hack.innovate2026.entity.Invoice;
import com.hack.innovate2026.entity.User;
import com.hack.innovate2026.dto.response.AuditLogResponse;
import com.hack.innovate2026.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(
            Invoice invoice,
            User user,
            String action,
            String oldValue,
            String newValue
    ) {
        auditLogRepository.save(AuditLog.builder()
                .invoice(invoice)
                .user(user)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .build());
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getInvoiceLogs(Long invoiceId) {
        return auditLogRepository.findByInvoiceIdOrderByTimestampDesc(invoiceId).stream()
                .map(this::toResponse)
                .toList();
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getInvoice() != null ? log.getInvoice().getId() : null,
                log.getInvoice() != null ? log.getInvoice().getInvoiceId() : null,
                log.getUser() != null ? log.getUser().getId() : null,
                log.getUser() != null ? log.getUser().getEmail() : null,
                log.getAction(),
                log.getOldValue(),
                log.getNewValue(),
                log.getTimestamp()
        );
    }
}
