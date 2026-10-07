package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.request.LoginRequest;
import com.hack.innovate2026.dto.response.LoginResponse;
import com.hack.innovate2026.security.JwtService;
import com.hack.innovate2026.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public com.hack.innovate2026.dto.response.UserResponse currentUser(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        return new com.hack.innovate2026.dto.response.UserResponse(
                user.getId(), user.getEmail(), user.getRole().name()
        );
    }

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                );

        var authentication =
                authenticationManager.authenticate(authenticationToken);

        String token = jwtService.generateToken(
                authentication.getName()
        );

        return new LoginResponse(token);
    }
}
