package com.hack.innovate2026.repository;

import com.hack.innovate2026.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByInvoiceIdOrderByTimestampDesc(Long invoiceId);

    List<AuditLog> findAllByOrderByTimestampDesc();
}