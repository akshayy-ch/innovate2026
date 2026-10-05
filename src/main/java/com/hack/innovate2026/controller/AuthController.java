package com.hack.innovate2026.controller;

import com.hack.innovate2026.dto.request.LoginRequest;
import com.hack.innovate2026.dto.request.RegisterRequest;
import com.hack.innovate2026.dto.response.LoginResponse;
import com.hack.innovate2026.dto.response.RegisterResponse;
import com.hack.innovate2026.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }
}