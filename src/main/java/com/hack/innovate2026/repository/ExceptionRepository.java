package com.hack.innovate2026.repository;

import com.hack.innovate2026.entity.ExceptionRecord;
import com.hack.innovate2026.enums.Decision;
import com.hack.innovate2026.enums.Severity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExceptionRepository
        extends JpaRepository<ExceptionRecord, Long> {

    List<ExceptionRecord> findByDecision(Decision decision);

    List<ExceptionRecord> findBySeverity(Severity severity);

    List<ExceptionRecord> findByInvoiceId(Long invoiceId);
}