package com.hack.innovate2026.dto.response;

import com.hack.innovate2026.enums.ReviewDecision;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long exceptionId,
        Long reviewerId,
        String reviewerEmail,
        ReviewDecision decision,
        String comments,
        LocalDateTime reviewedAt
) {}
