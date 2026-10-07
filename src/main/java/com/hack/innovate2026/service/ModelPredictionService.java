package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.response.ModelPredictionResponse;
import com.hack.innovate2026.entity.ModelPrediction;
import com.hack.innovate2026.repository.ModelPredictionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModelPredictionService {

    private final ModelPredictionRepository modelPredictionRepository;

    @Transactional(readOnly = true)
    public List<ModelPredictionResponse> getAll() {
        return modelPredictionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ModelPredictionResponse> getByInvoice(Long invoiceId) {
        return modelPredictionRepository.findByInvoiceId(invoiceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ModelPredictionResponse toResponse(ModelPrediction prediction) {
        return new ModelPredictionResponse(
                prediction.getId(),
                prediction.getInvoice().getId(),
                prediction.getInvoice().getInvoiceId(),
                prediction.getModelType(),
                prediction.getModelVersion(),
                prediction.getScore(),
                prediction.getProcessingTimeMs(),
                prediction.getCreatedAt()
        );
    }
}
