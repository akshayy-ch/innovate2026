package com.hack.innovate2026.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hack.innovate2026.dto.request.CreateInvoiceRequest;
import com.hack.innovate2026.dto.response.InvoiceResponse;
import com.hack.innovate2026.entity.Invoice;
import com.hack.innovate2026.entity.User;
import com.hack.innovate2026.enums.InvoiceStatus;
import com.hack.innovate2026.ml.MlAnalysisRequest;
import com.hack.innovate2026.ml.MlClient;
import com.hack.innovate2026.repository.InvoiceRepository;
import com.hack.innovate2026.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final MlClient mlClient;

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

        String status = analysis.path("status").asText("FLAGGED");

        if ("FLAGGED".equalsIgnoreCase(status)) {
            savedInvoice.setStatus(InvoiceStatus.HUMAN_REVIEW);
        } else {
            savedInvoice.setStatus(InvoiceStatus.AUTO_PASS);
        }

        savedInvoice = invoiceRepository.save(savedInvoice);

        auditLogService.log(
                savedInvoice,
                uploader,
                "ML_ANALYSIS_COMPLETED",
                null,
                "ml_status=" + status
        );

        return toResponse(savedInvoice);
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
