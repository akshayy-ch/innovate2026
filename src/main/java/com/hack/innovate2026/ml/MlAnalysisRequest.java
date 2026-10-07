package com.hack.innovate2026.ml;

import java.util.List;
import java.util.Map;

public record MlAnalysisRequest(
        List<InvoiceInput> invoices,
        List<InvoiceInput> history,
        Map<String, Double> department_limits,
        List<String> watchlist_tax_ids,
        ReviewPolicy review_policy,
        String window_id
) {
    public record InvoiceInput(
            String id,
            String invoice_number,
            String supplier,
            String supplier_id,
            String department,
            double amount,
            String currency,
            String date,
            String description,
            String po_number,
            String tax_id
    ) {}

    public record ReviewPolicy(
            int reviews_per_million,
            int monitors_per_million,
            double review_cost_inr,
            double recovery_fraction,
            Map<String, Double> currency_to_inr
    ) {}
}
