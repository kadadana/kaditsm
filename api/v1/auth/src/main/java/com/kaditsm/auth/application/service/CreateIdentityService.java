package com.kaditsm.auth.application.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kaditsm.auth.domain.exception.EmailAlreadyExistsException;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.port.in.CreateIdentityUseCase;
import com.kaditsm.auth.domain.port.out.IdentityRepositoryPort;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CreateIdentityService implements CreateIdentityUseCase {
    private final IdentityRepositoryPort identityRepository;
    private final PasswordEncoder passwordEncoder;

    public CreateIdentityService(IdentityRepositoryPort identityRepository, PasswordEncoder passwordEncoder) {
        this.identityRepository = identityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Identity create(CreateIdentityCommand command) {
        if (identityRepository.findByEmail(command.email()).isPresent()) {
            throw new EmailAlreadyExistsException("An identity with the provided email already exists.");
        }

        String passwordHash = passwordEncoder.encode(command.rawPassword());
        Identity identity = Identity.create(command.tenantId(), command.email(), passwordHash);

        return identityRepository.save(identity);
    }
}
