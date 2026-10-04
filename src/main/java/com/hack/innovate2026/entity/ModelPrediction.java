package com.hack.innovate2026.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "model_predictions",
        indexes = {
                @Index(name = "idx_prediction_invoice", columnList = "invoice_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "model_type", nullable = false, length = 100)
    private String modelType;

    @Column(name = "model_version", length = 100)
    private String modelVersion;

    @Column(precision = 8, scale = 6)
    private BigDecimal score;

    @Column(name = "processing_time_ms")
    private Long processingTimeMs;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}