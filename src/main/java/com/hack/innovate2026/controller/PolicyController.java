package com.hack.innovate2026.controller;

import com.hack.innovate2026.dto.request.CreatePolicyRequest;
import com.hack.innovate2026.dto.request.UpdatePolicyRequest;
import com.hack.innovate2026.dto.response.PolicyResponse;
import com.hack.innovate2026.service.PolicyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST','MANAGER','ADMIN')")
    public ResponseEntity<List<PolicyResponse>> getAllPolicies(
            @RequestParam(required = false) String category
    ) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(policyService.getPoliciesByCategory(category));
        }
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST','MANAGER','ADMIN')")
    public ResponseEntity<PolicyResponse> getPolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponse> createPolicy(
            @Valid @RequestBody CreatePolicyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(policyService.createPolicy(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PolicyResponse> updatePolicy(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePolicyRequest request
    ) {
        return ResponseEntity.ok(policyService.updatePolicy(id, request));
    }
}
