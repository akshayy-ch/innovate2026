package com.hack.innovate2026.controller;

import com.hack.innovate2026.dto.request.CreateExceptionRequest;
import com.hack.innovate2026.dto.request.CreateReviewRequest;
import com.hack.innovate2026.dto.response.ExceptionResponse;
import com.hack.innovate2026.dto.response.ReviewResponse;
import com.hack.innovate2026.service.ExceptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exceptions")
@RequiredArgsConstructor
public class ExceptionController {

    private final ExceptionService exceptionService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST','MANAGER','ADMIN')")
    public ResponseEntity<List<ExceptionResponse>> getAllExceptions() {
        return ResponseEntity.ok(exceptionService.getAllExceptions());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ANALYST','MANAGER','ADMIN')")
    public ResponseEntity<ExceptionResponse> createException(
            @Valid @RequestBody CreateExceptionRequest request
    ) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
                .body(exceptionService.createException(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST','MANAGER','ADMIN')")
    public ResponseEntity<ExceptionResponse> getExceptionById(@PathVariable Long id) {
        return ResponseEntity.ok(exceptionService.getExceptionById(id));
    }

    @PostMapping("/{id}/reviews")
    @PreAuthorize("hasAnyRole('ANALYST','MANAGER','ADMIN')")
    public ResponseEntity<ReviewResponse> reviewException(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewRequest request,
            org.springframework.security.core.Authentication authentication
    ) {
        return ResponseEntity.ok(exceptionService.reviewException(id, request, authentication));
    }
}
