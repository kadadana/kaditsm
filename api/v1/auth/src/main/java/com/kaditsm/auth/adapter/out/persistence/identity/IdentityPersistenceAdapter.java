package com.kaditsm.auth.adapter.out.persistence.identity;

import com.kaditsm.auth.adapter.out.persistence.identity.mapper.IdentityEntityMapper;
import com.kaditsm.auth.application.port.out.IdentityRepositoryPort;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.PagedResult;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class IdentityPersistenceAdapter implements IdentityRepositoryPort {

    private final IdentityJpaRepository identityJpaRepository;
    private final IdentityEntityMapper identityEntityMapper;

    public IdentityPersistenceAdapter(
            IdentityJpaRepository identityJpaRepository,
            IdentityEntityMapper identityEntityMapper) {
        this.identityJpaRepository = identityJpaRepository;
        this.identityEntityMapper = identityEntityMapper;
    }

    @Override
    public Optional<Identity> findByEmail(String email) {
        return identityJpaRepository.findByEmail(email)
                .map(identityEntityMapper::toDomain);
    }

    @Override
    public Optional<Identity> findById(UUID id) {
        return identityJpaRepository.findById(id)
                .map(identityEntityMapper::toDomain);
    }

    @Override
    public Optional<Identity> findByEmailAndTenantId(String email, UUID tenantId) {
        return identityJpaRepository.findByEmailAndTenantId(email, tenantId)
                .map(identityEntityMapper::toDomain);
    }

    @Override
    public Identity save(Identity identity) {
        IdentityJpaEntity entity = identityEntityMapper.toJpaEntity(identity);
        IdentityJpaEntity savedEntity = identityJpaRepository.save(entity);
        return identityEntityMapper.toDomain(savedEntity);
    }

    @Override
    public PagedResult<Identity> findAllByTenantId(UUID tenantId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<IdentityJpaEntity> jpaPage = identityJpaRepository.findAllByTenantId(tenantId, pageRequest);
        List<Identity> domainList = jpaPage.getContent().stream()
                .map(identityEntityMapper::toDomain)
                .toList();

        return new PagedResult<>(
                domainList,
                jpaPage.getNumber(),
                jpaPage.getSize(),
                jpaPage.getTotalElements(),
                jpaPage.getTotalPages());
    }
}