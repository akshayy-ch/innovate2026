package com.hack.innovate2026.entity;

import com.hack.innovate2026.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "invoices",
        indexes = {
                @Index(name = "idx_invoice_vendor", columnList = "vendor_name"),
                @Index(name = "idx_invoice_status", columnList = "status"),
                @Index(name = "idx_invoice_date", columnList = "invoice_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_id", nullable = false, unique = true, length = 100)
    private String invoiceId;

    @Column(name = "vendor_name", length = 255)
    private String vendorName;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String category;

    @Column(name = "employee_name", length = 255)
    private String employeeName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private InvoiceStatus status = InvoiceStatus.PENDING;

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

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private User uploadedBy;
}