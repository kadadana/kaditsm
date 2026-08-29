package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.port.in.UpdateIdentityUseCase;
import com.kaditsm.auth.domain.port.out.IdentityRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateIdentityService implements UpdateIdentityUseCase {

    private final IdentityRepositoryPort identityRepository;
    private final PasswordEncoder passwordEncoder;

    public UpdateIdentityService(IdentityRepositoryPort identityRepository, PasswordEncoder passwordEncoder) {
        this.identityRepository = identityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Identity updateIdentity(UpdateIdentityCommand command) {
        Identity identity = identityRepository.findById(command.accountId())
                .orElseThrow(() -> new IllegalArgumentException("Identity not found: " + command.accountId()));

        Identity updated = identity;

        if (command.newPassword() != null && !command.newPassword().isBlank()) {
            if (command.currentPassword() == null
                    || !passwordEncoder.matches(command.currentPassword(), identity.getPasswordHash())) {
                throw new IllegalArgumentException("Invalid current password");
            }
            updated = updated.withPasswordHash(passwordEncoder.encode(command.newPassword()));
        }

        if (command.email() != null && !command.email().isBlank()) {
            updated = updated.withEmail(command.email());
        }

        return identityRepository.save(updated);
    }
}