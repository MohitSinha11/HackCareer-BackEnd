package com.hackcareer.backend.auth;

import com.hackcareer.backend.common.BadRequestException;
import com.hackcareer.backend.common.NotFoundException;
import com.hackcareer.backend.domain.entity.User;
import com.hackcareer.backend.domain.enums.Role;
import com.hackcareer.backend.domain.repository.UserRepository;
import com.hackcareer.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (user.getRole() != request.role()) {
            throw new BadRequestException("Selected role does not match this account");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new AuthDtos.LoginResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public AuthDtos.LoginResponse signupAdmin(AuthDtos.AdminSignupRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email already exists");
        }

        User user = userRepository.save(User.builder()
                .fullName(request.fullName().trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ADMIN)
                .enabled(true)
                .build());

        String token = jwtService.generateToken(user.getEmail(), user.getRole().name());

        return new AuthDtos.LoginResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
