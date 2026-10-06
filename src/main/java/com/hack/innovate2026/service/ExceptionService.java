package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.request.CreateReviewRequest;
import com.hack.innovate2026.dto.response.ReviewResponse;
import com.hack.innovate2026.entity.ExceptionRecord;
import com.hack.innovate2026.entity.Review;
import com.hack.innovate2026.entity.User;
import com.hack.innovate2026.enums.Decision;
import com.hack.innovate2026.enums.UserRole;
import com.hack.innovate2026.repository.ExceptionRepository;
import com.hack.innovate2026.repository.ReviewRepository;
import com.hack.innovate2026.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExceptionService {

    private final ExceptionRepository exceptionRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;

    public List<ExceptionRecord> getAllExceptions() {
        return exceptionRepository.findAll();
    }

    public ExceptionRecord getExceptionById(Long id) {
        return exceptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exception not found with id: " + id));
    }

    @Transactional
    public ReviewResponse reviewException(
            Long exceptionId,
            CreateReviewRequest request,
            Authentication authentication
    ) {
        ExceptionRecord exception = getExceptionById(exceptionId);

        User reviewer = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        UserRole role = reviewer.getRole();

        if (exception.getInvoice().getUploadedBy() != null
                && exception.getInvoice().getUploadedBy().getId().equals(reviewer.getId())) {
            throw new IllegalStateException("Maker-checker violation: invoice uploader cannot review its exception");
        }

        if (!canReview(role, exception.getDecision())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Role " + role + " cannot review exception with decision " + exception.getDecision()
            );
        }

        Review review = Review.builder()
                .exception(exception)
                .reviewer(reviewer)
                .decision(request.decision())
                .comments(request.comments())
                .build();

        Review saved = reviewRepository.save(review);

        return new ReviewResponse(
                saved.getId(),
                exception.getId(),
                reviewer.getId(),
                reviewer.getEmail(),
                saved.getDecision(),
                saved.getComments(),
                saved.getReviewedAt()
        );
    }

    private boolean canReview(UserRole role, Decision decision) {
        if (role == UserRole.ADMIN) {
            return true;
        }

        if (role == UserRole.ANALYST) {
            return decision == Decision.HUMAN_REVIEW;
        }

        if (role == UserRole.MANAGER) {
            return decision == Decision.HIGH_RISK || decision == Decision.HOLD;
        }

        return false;
    }
}
