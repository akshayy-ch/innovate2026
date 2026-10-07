package com.hack.innovate2026.dto.response;

import java.util.List;

public record BatchInvoiceUploadResponse(
        int totalRows,
        int created,
        int skipped,
        List<String> skippedInvoiceIds,
        List<InvoiceResponse> invoices
) {}
