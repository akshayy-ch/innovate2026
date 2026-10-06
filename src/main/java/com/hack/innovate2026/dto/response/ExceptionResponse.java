package com.hack.innovate2026.dto.response;

import com.hack.innovate2026.enums.Decision;
import com.hack.innovate2026.enums.Severity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExceptionResponse(
        Long id,
        Long invoiceId,
        String invoiceNumber,
        String exceptionType,
        Severity severity,
        BigDecimal confidence,
        BigDecimal riskScore,
        String reason,
        String matchedRecordId,
        Decision decision,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
