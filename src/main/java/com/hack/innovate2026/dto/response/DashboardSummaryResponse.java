package com.hack.innovate2026.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record DashboardSummaryResponse(
        long totalInvoices,
        long pendingInvoices,
        long autoPassedInvoices,
        long humanReviewInvoices,
        long highRiskInvoices,
        long resolvedInvoices,
        long totalExceptions,
        long highSeverityExceptions,
        long mediumSeverityExceptions,
        long lowSeverityExceptions,
        BigDecimal totalInvoiceAmount,
        Map<String, Long> exceptionsByType,
        Map<String, Long> exceptionsByDecision
) {}
