package com.hack.innovate2026.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateInvoiceRequest(
        @NotBlank(message = "Invoice ID is required")
        String invoiceId,

        String vendorName,

        @DecimalMin(value = "0.0", inclusive = true, message = "Amount cannot be negative")
        BigDecimal amount,

        LocalDate invoiceDate,

        String description,

        String category,

        String employeeName,

        @NotNull(message = "Invoice status is required")
        com.hack.innovate2026.enums.InvoiceStatus status
) {}
