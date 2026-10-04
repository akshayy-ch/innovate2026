package com.hack.innovate2026.repository;

import com.hack.innovate2026.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByInvoiceId(String invoiceId);

    boolean existsByInvoiceId(String invoiceId);
}