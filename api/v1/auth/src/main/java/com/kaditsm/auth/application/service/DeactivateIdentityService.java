package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.port.in.DeactivateIdentityUseCase;
import com.kaditsm.auth.domain.port.out.IdentityRepositoryPort;

import jakarta.transaction.Transactional;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class DeactivateIdentityService implements DeactivateIdentityUseCase {

    private final IdentityRepositoryPort identityRepository;

    public DeactivateIdentityService(IdentityRepositoryPort identityRepository) {
        this.identityRepository = identityRepository;
    }

    @Override
    public void deactivate(UUID accountId) {
        Identity identity = identityRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Identity not found: " + accountId));

        Identity deactivated = identity.deactivate();
        identityRepository.save(deactivated);
    }
}
