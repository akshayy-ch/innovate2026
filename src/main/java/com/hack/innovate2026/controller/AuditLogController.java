package com.hack.innovate2026.controller;

import com.hack.innovate2026.service.AuditLogService;
import com.hack.innovate2026.dto.response.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<AuditLogResponse> getAllLogs() {
        return auditLogService.getAllLogs();
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public List<AuditLog> getInvoiceLogs(@PathVariable Long invoiceId) {
        return auditLogService.getInvoiceLogs(invoiceId);
    }
}
