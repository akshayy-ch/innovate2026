package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.request.CreateExceptionRequest;
import com.hack.innovate2026.dto.request.CreateReviewRequest;
import com.hack.innovate2026.dto.response.ExceptionResponse;
import com.hack.innovate2026.dto.response.ReviewResponse;
import com.hack.innovate2026.entity.ExceptionRecord;
import com.hack.innovate2026.entity.Review;
import com.hack.innovate2026.entity.Invoice;
import com.hack.innovate2026.entity.User;
import com.hack.innovate2026.enums.Decision;
import com.hack.innovate2026.enums.InvoiceStatus;
import com.hack.innovate2026.enums.UserRole;
import com.hack.innovate2026.repository.ExceptionRepository;
import com.hack.innovate2026.repository.InvoiceRepository;
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
    private final InvoiceRepository invoiceRepository;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    public List<ExceptionResponse> getAllExceptions() {
        return exceptionRepository.findAll().stream().map(this::toResponse).toList();
    }

    public ExceptionResponse getExceptionById(Long id) {
        return toResponse(getExceptionRecordById(id));
    }

    @Transactional
    public ExceptionResponse createException(CreateExceptionRequest request) {
        Invoice invoice = invoiceRepository.findById(request.invoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + request.invoiceId()));

        ExceptionRecord exception = ExceptionRecord.builder()
                .invoice(invoice)
                .exceptionType(request.exceptionType())
                .severity(request.severity())
                .confidence(request.confidence())
                .riskScore(request.riskScore())
                .reason(request.reason())
                .matchedRecordId(request.matchedRecordId())
                .decision(request.decision())
                .build();

        invoice.setStatus(statusForDecision(request.decision()));
        invoiceRepository.save(invoice);

        ExceptionRecord savedException = exceptionRepository.save(exception);
        auditLogService.log(
                invoice,
                null,
                "EXCEPTION_CREATED",
                null,
                "exceptionId=" + savedException.getId() + ",decision=" + savedException.getDecision()
        );
        return toResponse(savedException);
    }

    private ExceptionRecord getExceptionRecordById(Long id) {
        return exceptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exception not found with id: " + id));
    }

    private ExceptionResponse toResponse(ExceptionRecord exception) {
        return new ExceptionResponse(
                exception.getId(),
                exception.getInvoice().getId(),
                exception.getInvoice().getInvoiceId(),
                exception.getExceptionType(),
                exception.getSeverity(),
                exception.getConfidence(),
                exception.getRiskScore(),
                exception.getReason(),
                exception.getMatchedRecordId(),
                exception.getDecision(),
                exception.getCreatedAt(),
                exception.getUpdatedAt()
        );
    }

    @Transactional
    public ReviewResponse reviewException(
            Long exceptionId,
            CreateReviewRequest request,
            Authentication authentication
    ) {
        ExceptionRecord exception = getExceptionRecordById(exceptionId);

        User reviewer = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        UserRole role = reviewer.getRole();

        if (exception.getInvoice().getUploadedBy() != null
                && exception.getInvoice().getUploadedBy().getId().equals(reviewer.getId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Maker-checker violation: invoice uploader cannot review its exception"
            );
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

        exception.getInvoice().setStatus(InvoiceStatus.RESOLVED);
        invoiceRepository.save(exception.getInvoice());

        auditLogService.log(
                exception.getInvoice(),
                reviewer,
                "EXCEPTION_REVIEWED",
                "decision=" + exception.getDecision(),
                "reviewDecision=" + saved.getDecision()
        );

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

    private InvoiceStatus statusForDecision(Decision decision) {
        return switch (decision) {
            case AUTO_PASS -> InvoiceStatus.AUTO_PASS;
            case HUMAN_REVIEW -> InvoiceStatus.HUMAN_REVIEW;
            case HIGH_RISK, HOLD -> InvoiceStatus.HIGH_RISK;
        };
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
