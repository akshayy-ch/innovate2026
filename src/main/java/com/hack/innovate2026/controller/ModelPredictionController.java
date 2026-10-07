package com.hack.innovate2026.controller;

import com.hack.innovate2026.dto.response.ModelPredictionResponse;
import com.hack.innovate2026.service.ModelPredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model-predictions")
@RequiredArgsConstructor
public class ModelPredictionController {

    private final ModelPredictionService modelPredictionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST','MANAGER','ADMIN')")
    public ResponseEntity<List<ModelPredictionResponse>> getAll() {
        return ResponseEntity.ok(modelPredictionService.getAll());
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('ANALYST','MANAGER','ADMIN')")
    public ResponseEntity<List<ModelPredictionResponse>> getByInvoice(
            @PathVariable Long invoiceId
    ) {
        return ResponseEntity.ok(modelPredictionService.getByInvoice(invoiceId));
    }
}
