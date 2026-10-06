package com.hack.innovate2026.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record CreatePolicyRequest(
        @NotBlank(message = "Category is required")
        String category,

        @DecimalMin(value = "0.0", inclusive = true, message = "Max amount cannot be negative")
        BigDecimal maxAmount,

        @DecimalMin(value = "0.0", inclusive = true, message = "Company limit cannot be negative")
        BigDecimal companyLimit
) {}
