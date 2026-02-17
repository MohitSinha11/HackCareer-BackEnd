package com.hackcareer.backend.auth;

import com.hackcareer.backend.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AuthDtos {

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password,
            @NotNull Role role
    ) {
    }

    public record LoginResponse(
            String token,
            Long userId,
            String fullName,
            String email,
            Role role
    ) {
    }

    public record AdminSignupRequest(
            @NotBlank String fullName,
            @Email @NotBlank String email,
            @NotBlank String password
    ) {
    }
}
