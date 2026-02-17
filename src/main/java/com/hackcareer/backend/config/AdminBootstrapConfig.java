package com.hackcareer.backend.config;

import com.hackcareer.backend.domain.entity.User;
import com.hackcareer.backend.domain.enums.Role;
import com.hackcareer.backend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminBootstrapConfig {

    @Value("${app.bootstrap.admin.email}")
    private String email;

    @Value("${app.bootstrap.admin.password}")
    private String password;

    @Value("${app.bootstrap.admin.name:Default Admin}")
    private String name;

    @Bean
    CommandLineRunner seedAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (!userRepository.existsByEmail(email)) {
                User admin = User.builder()
                        .fullName(name)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .role(Role.ADMIN)
                        .enabled(true)
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
