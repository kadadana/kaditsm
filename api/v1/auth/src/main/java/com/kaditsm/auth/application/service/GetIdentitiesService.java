package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.exception.UserNotFoundException;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.PagedResult;
import com.kaditsm.auth.domain.port.in.GetIdentitiesUseCase;
import com.kaditsm.auth.domain.port.out.IdentityRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class GetIdentitiesService implements GetIdentitiesUseCase {

    private final IdentityRepositoryPort identityRepository;

    public GetIdentitiesService(IdentityRepositoryPort identityRepository) {
        this.identityRepository = identityRepository;
    }

    @Override
    public PagedResult<Identity> getIdentities(UUID tenantId, int page, int size) {
        return identityRepository.findAllByTenantId(tenantId, page, size);
    }

    @Override
    public Identity getIdentityById(UUID id) {
        return identityRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Identity not found with id: " + id));
    }
}