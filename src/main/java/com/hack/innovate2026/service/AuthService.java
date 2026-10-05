package com.hack.innovate2026.service;

import com.hack.innovate2026.dto.request.LoginRequest;
import com.hack.innovate2026.dto.response.LoginResponse;
import com.hack.innovate2026.dto.request.RegisterRequest;
import com.hack.innovate2026.dto.response.RegisterResponse;
import com.hack.innovate2026.entity.User;
import com.hack.innovate2026.repository.UserRepository;
import com.hack.innovate2026.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();

        user.setEmail(request.email());
        user.setPasswordHash(
                passwordEncoder.encode(request.password())
        );
        user.setRole(request.role());

        User savedUser = userRepository.save(user);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }
}