package com.hack.innovate2026.dto.request;

import com.hack.innovate2026.enums.Decision;
import com.hack.innovate2026.enums.Severity;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateExceptionRequest(
        @NotNull(message = "Invoice ID is required")
        Long invoiceId,

        @NotBlank(message = "Exception type is required")
        String exceptionType,

        Severity severity,

        @DecimalMin(value = "0.0", message = "Confidence cannot be negative")
        @DecimalMax(value = "1.0", message = "Confidence cannot exceed 1.0")
        BigDecimal confidence,

        @DecimalMin(value = "0.0", message = "Risk score cannot be negative")
        @DecimalMax(value = "100.0", message = "Risk score cannot exceed 100")
        BigDecimal riskScore,

        String reason,

        String matchedRecordId,

        @NotNull(message = "Decision is required")
        Decision decision
) {}
