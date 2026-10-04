package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.response.InvoiceResponse;
import com.hack.innovate2026.entity.Invoice;
import com.hack.innovate2026.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found with id: " + id)
                );

        return toResponse(invoice);
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