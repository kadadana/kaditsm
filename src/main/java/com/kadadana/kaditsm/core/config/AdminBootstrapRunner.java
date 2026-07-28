package com.kadadana.kaditsm.core.config;

import com.kadadana.kaditsm.modules.auth.entity.AuthEntity;
import com.kadadana.kaditsm.modules.auth.repository.AuthRepository;
import com.kadadana.kaditsm.modules.user.entity.UserEntity;
import com.kadadana.kaditsm.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Configuration
public class AdminBootstrapRunner {

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    @Bean
    @Transactional
    public ApplicationRunner adminBootstrap(
            UserRepository userRepository,
            AuthRepository authRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0 && authRepository.count() == 0) {
                UUID adminId = UUID.randomUUID();
                String encodedPassword = passwordEncoder.encode(adminPassword);

                UserEntity adminUser = UserEntity.builder()
                        .id(adminId)
                        .username(adminUsername)
                        .displayName("Administrator")
                        .department("IT")
                        .email("admin@example.com")
                        .role("ADMIN")
                        .build();

                AuthEntity adminAuth = AuthEntity.builder()
                        .id(adminId)
                        .password(encodedPassword)
                        .build();

                userRepository.save(adminUser);
                authRepository.save(adminAuth);
            }
        };
    }
}