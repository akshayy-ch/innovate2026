package com.hack.innovate2026.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PolicyResponse(
        Long id,
        String category,
        BigDecimal maxAmount,
        BigDecimal companyLimit,
        Boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
