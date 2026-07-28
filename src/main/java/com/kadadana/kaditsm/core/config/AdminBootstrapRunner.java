package com.kadadana.kaditsm.core.config;

import com.kadadana.kaditsm.modules.user.entity.UserEntity;
import com.kadadana.kaditsm.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class AdminBootstrapRunner {

    @Value("${app.admin.username:admin}")
    private String adminUsername;

    @Value("${app.admin.password:admin}")
    private String adminPassword;

    @Bean
    public ApplicationRunner adminBootstrap(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                UserEntity admin = UserEntity.builder()
                        .id(UUID.randomUUID().toString())
                        .username(adminUsername)
                        .displayName("Administrator")
                        .department("IT")
                        .email("admin@example.com")
                        .role("ADMIN")
                        .password(passwordEncoder.encode(adminPassword))
                        .build();
                userRepository.save(admin);
            }
        };
    }
}
