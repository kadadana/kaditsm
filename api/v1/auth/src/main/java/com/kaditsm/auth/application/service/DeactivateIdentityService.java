package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.exception.UserNotFoundException;
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
    public void deactivate(UUID identityId) {
        Identity identity = identityRepository.findById(identityId)
                .orElseThrow(() -> new UserNotFoundException("Identity not found with id: " + identityId));

        Identity deactivated = identity.deactivate();
        identityRepository.save(deactivated);
    }
}
