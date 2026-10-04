package com.hack.innovate2026.repository;

import com.hack.innovate2026.entity.ModelPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelPredictionRepository
        extends JpaRepository<ModelPrediction, Long> {

    List<ModelPrediction> findByInvoiceId(Long invoiceId);
}