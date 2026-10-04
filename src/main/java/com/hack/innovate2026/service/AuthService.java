package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.request.LoginRequest;
import com.hack.innovate2026.dto.response.LoginResponse;
import com.hack.innovate2026.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                );

        Authentication authentication =
                authenticationManager.authenticate(authenticationToken);

        String token = jwtService.generateToken(
                authentication.getName()
        );

        return new LoginResponse(token);
    }
}