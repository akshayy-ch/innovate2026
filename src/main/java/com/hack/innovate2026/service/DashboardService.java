package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.response.DashboardSummaryResponse;
import com.hack.innovate2026.enums.Decision;
import com.hack.innovate2026.enums.InvoiceStatus;
import com.hack.innovate2026.enums.Severity;
import com.hack.innovate2026.repository.ExceptionRepository;
import com.hack.innovate2026.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final ExceptionRepository exceptionRepository;

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        var invoices = invoiceRepository.findAll();
        var exceptions = exceptionRepository.findAll();

        long pending = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.PENDING).count();
        long autoPassed = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.AUTO_PASS).count();
        long humanReview = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.HUMAN_REVIEW).count();
        long highRisk = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.HIGH_RISK).count();
        long resolved = invoices.stream().filter(i -> i.getStatus() == InvoiceStatus.RESOLVED).count();

        long highSeverity = exceptions.stream().filter(e -> e.getSeverity() == Severity.HIGH).count();
        long mediumSeverity = exceptions.stream().filter(e -> e.getSeverity() == Severity.MEDIUM).count();
        long lowSeverity = exceptions.stream().filter(e -> e.getSeverity() == Severity.LOW).count();

        BigDecimal totalAmount = invoices.stream()
                .map(i -> i.getAmount() == null ? BigDecimal.ZERO : i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> byType = exceptions.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getExceptionType(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        Map<String, Long> byDecision = exceptions.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDecision() == null ? "UNASSIGNED" : e.getDecision().name(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        return new DashboardSummaryResponse(
                invoices.size(),
                pending,
                autoPassed,
                humanReview,
                highRisk,
                resolved,
                exceptions.size(),
                highSeverity,
                mediumSeverity,
                lowSeverity,
                totalAmount,
                byType,
                byDecision
        );
    }
}
