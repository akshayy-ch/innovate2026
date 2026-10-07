package com.hack.innovate2026.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hack.innovate2026.dto.request.CreateInvoiceRequest;
import com.hack.innovate2026.dto.response.InvoiceResponse;
import com.hack.innovate2026.entity.ExceptionRecord;
import com.hack.innovate2026.entity.Invoice;
import com.hack.innovate2026.entity.ModelPrediction;
import com.hack.innovate2026.entity.User;
import com.hack.innovate2026.enums.Decision;
import com.hack.innovate2026.enums.InvoiceStatus;
import com.hack.innovate2026.enums.Severity;
import com.hack.innovate2026.ml.MlAnalysisRequest;
import com.hack.innovate2026.ml.MlClient;
import com.hack.innovate2026.repository.ExceptionRepository;
import com.hack.innovate2026.repository.InvoiceRepository;
import com.hack.innovate2026.repository.ModelPredictionRepository;
import com.hack.innovate2026.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final MlClient mlClient;
    private final ExceptionRepository exceptionRepository;
    private final ModelPredictionRepository modelPredictionRepository;

    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new com.hack.innovate2026.exception.ResourceNotFoundException("Invoice not found with id: " + id)
                );

        return toResponse(invoice);
    }

    public InvoiceResponse createInvoice(
            CreateInvoiceRequest request,
            Authentication authentication
    ) {
        if (invoiceRepository.existsByInvoiceId(request.invoiceId())) {
            throw new com.hack.innovate2026.exception.ConflictException("Invoice ID already exists: " + request.invoiceId());
        }

        User uploader = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

        Invoice invoice = Invoice.builder()
                .invoiceId(request.invoiceId())
                .vendorName(request.vendorName())
                .amount(request.amount())
                .invoiceDate(request.invoiceDate())
                .description(request.description())
                .category(request.category())
                .employeeName(request.employeeName())
                .status(InvoiceStatus.PENDING)
                .uploadedBy(uploader)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        auditLogService.log(
                savedInvoice,
                uploader,
                "INVOICE_CREATED",
                null,
                "status=PENDING"
        );

        MlAnalysisRequest.InvoiceInput mlInvoice =
                new MlAnalysisRequest.InvoiceInput(
                        savedInvoice.getInvoiceId(),
                        savedInvoice.getInvoiceId(),
                        savedInvoice.getVendorName(),
                        savedInvoice.getVendorName(),
                        savedInvoice.getCategory(),
                        savedInvoice.getAmount().doubleValue(),
                        "INR",
                        savedInvoice.getInvoiceDate().toString(),
                        savedInvoice.getDescription(),
                        "",
                        ""
                );

        MlAnalysisRequest mlRequest =
                new MlAnalysisRequest(
                        List.of(mlInvoice),
                        List.of(),
                        Map.of(),
                        List.of(),
                        new MlAnalysisRequest.ReviewPolicy(
                                100,
                                100,
                                100,
                                0.5,
                                Map.of("INR", 1.0)
                        ),
                        "invoice-" + savedInvoice.getId()
                );

        JsonNode analysis = mlClient.analyze(mlRequest);
        persistAnalysis(savedInvoice, analysis);

        savedInvoice = invoiceRepository.save(savedInvoice);

        auditLogService.log(
                savedInvoice,
                uploader,
                "ML_ANALYSIS_COMPLETED",
                null,
                "ml_status=" + analysis.path("status").asText("FLAGGED")
        );

        return toResponse(savedInvoice);
    }

    private void persistAnalysis(Invoice invoice, JsonNode analysis) {
        String status = analysis.path("status").asText("FLAGGED");
        boolean reviewRequired = analysis.path("review_required").asBoolean(false);

        if ("FLAGGED".equalsIgnoreCase(status) || reviewRequired) {
            invoice.setStatus(InvoiceStatus.HUMAN_REVIEW);
        } else {
            invoice.setStatus(InvoiceStatus.AUTO_PASS);
        }

        int riskScore = analysis.path("risk_score").asInt(0);
        String severityText = analysis.path("severity").asText("MEDIUM");
        Severity severity = parseSeverity(severityText);

        JsonNode signals = analysis.path("signals");
        if (signals.isArray() && !signals.isEmpty()) {
            for (JsonNode signal : signals) {
                String code = signal.path("code").asText("AP_EXCEPTION");
                String reason = signal.path("reason").asText("Potential exception requires verification.");
                String signalSeverity = signal.path("severity").asText(severityText);

                exceptionRepository.save(
                        ExceptionRecord.builder()
                                .invoice(invoice)
                                .exceptionType(code)
                                .severity(parseSeverity(signalSeverity))
                                .confidence(null)
                                .riskScore(BigDecimal.valueOf(riskScore))
                                .reason(reason)
                                .matchedRecordId(null)
                                .decision(toDecision(invoice.getStatus()))
                                .build()
                );
            }
        } else if (reviewRequired) {
            exceptionRepository.save(
                    ExceptionRecord.builder()
                            .invoice(invoice)
                            .exceptionType("AP_EXCEPTION")
                            .severity(severity)
                            .confidence(null)
                            .riskScore(BigDecimal.valueOf(riskScore))
                            .reason("Potential exception requires verification.")
                            .decision(toDecision(invoice.getStatus()))
                            .build()
            );
        }

        boolean mlExecuted = analysis.path("cascade").path("ml_executed").asInt(0) > 0
                || analysis.path("pipeline").path("ml_executed").asBoolean(false);

        if (mlExecuted) {
            JsonNode model = analysis.path("model_prediction");
            if (!model.isMissingNode() && !model.isNull()) {
                double score = model.path("score").asDouble(-1);
                if (score >= 0) {
                    modelPredictionRepository.save(
                            ModelPrediction.builder()
                                    .invoice(invoice)
                                    .modelType(model.path("model_type").asText("AP_ML"))
                                    .modelVersion(model.path("model_version").asText(null))
                                    .score(BigDecimal.valueOf(score))
                                    .processingTimeMs(
                                            model.has("processing_time_ms")
                                                    ? model.path("processing_time_ms").asLong()
                                                    : null
                                    )
                                    .build()
                    );
                }
            }
        }
    }

    private Severity parseSeverity(String value) {
        try {
            return Severity.valueOf(value.toUpperCase());
        } catch (Exception ignored) {
            return Severity.MEDIUM;
        }
    }

    private Decision toDecision(InvoiceStatus status) {
        return switch (status) {
            case AUTO_PASS -> Decision.AUTO_PASS;
            case HUMAN_REVIEW -> Decision.HUMAN_REVIEW;
            case HIGH_RISK -> Decision.HIGH_RISK;
            case RESOLVED -> Decision.HOLD;
            default -> Decision.HUMAN_REVIEW;
        };
    }

    private InvoiceResponse toResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceId(),
                invoice.getVendorName(),
                invoice.getAmount(),
                invoice.getInvoiceDate(),
                invoice.getDescription(),
                invoice.getCategory(),
                invoice.getEmployeeName(),
                invoice.getStatus(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }
}
