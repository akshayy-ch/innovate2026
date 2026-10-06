package com.hack.innovate2026.dto.request;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record UpdatePolicyRequest(
        @DecimalMin(value = "0.0", inclusive = true, message = "Max amount cannot be negative")
        BigDecimal maxAmount,

        @DecimalMin(value = "0.0", inclusive = true, message = "Company limit cannot be negative")
        BigDecimal companyLimit,

        Boolean active
) {}
