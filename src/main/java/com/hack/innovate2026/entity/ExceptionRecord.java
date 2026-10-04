package com.hack.innovate2026.entity;

import com.hack.innovate2026.enums.Decision;
import com.hack.innovate2026.enums.Severity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "exceptions",
        indexes = {
                @Index(name = "idx_exception_invoice", columnList = "invoice_id"),
                @Index(name = "idx_exception_type", columnList = "exception_type"),
                @Index(name = "idx_exception_severity", columnList = "severity"),
                @Index(name = "idx_exception_decision", columnList = "decision"),
                @Index(name = "idx_exception_risk", columnList = "risk_score")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExceptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "exception_type", nullable = false, length = 100)
    private String exceptionType;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Severity severity;

    @Column(precision = 5, scale = 4)
    private BigDecimal confidence;

    @Column(name = "risk_score", precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "matched_record_id", length = 100)
    private String matchedRecordId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Decision decision;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}