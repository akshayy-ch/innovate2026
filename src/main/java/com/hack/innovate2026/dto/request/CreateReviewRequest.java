package com.hack.innovate2026.dto.request;

import com.hack.innovate2026.enums.ReviewDecision;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
        @NotNull(message = "Review decision is required")
        ReviewDecision decision,
        String comments
) {}
