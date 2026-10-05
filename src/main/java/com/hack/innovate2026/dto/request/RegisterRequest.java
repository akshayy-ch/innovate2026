package com.hack.innovate2026.dto.request;

import com.hack.innovate2026.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        String password,

        @NotNull(message = "Role is required")
        UserRole role
) {}