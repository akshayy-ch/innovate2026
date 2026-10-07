package com.hack.innovate2026.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ModelPredictionResponse(
        Long id,
        Long invoiceId,
        String invoiceNumber,
        String modelType,
        String modelVersion,
        BigDecimal score,
        Long processingTimeMs,
        LocalDateTime createdAt
) {}
