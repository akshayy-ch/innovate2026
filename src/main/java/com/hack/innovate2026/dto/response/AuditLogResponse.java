package com.hack.innovate2026.dto.response;

import java.time.LocalDateTime;

public record AuditLogResponse(
        Long id,
        Long invoiceId,
        String invoiceNumber,
        Long userId,
        String userEmail,
        String action,
        String oldValue,
        String newValue,
        LocalDateTime timestamp
) {}
