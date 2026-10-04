package com.hack.innovate2026.dto.response;

import com.hack.innovate2026.enums.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record InvoiceResponse(
        Long id,
        String invoiceId,
        String vendorName,
        BigDecimal amount,
        LocalDate invoiceDate,
        String description,
        String category,
        String employeeName,
        InvoiceStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}