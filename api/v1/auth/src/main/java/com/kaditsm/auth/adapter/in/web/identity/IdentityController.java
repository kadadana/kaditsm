package com.kaditsm.auth.adapter.in.web.identity;

import com.kaditsm.auth.adapter.in.web.identity.dto.CreateIdentityRequest;
import com.kaditsm.auth.adapter.in.web.identity.dto.IdentityResponse;
import com.kaditsm.auth.adapter.in.web.identity.dto.PagedResponse;
import com.kaditsm.auth.adapter.in.web.identity.dto.UpdateIdentity;
import com.kaditsm.auth.adapter.in.web.identity.mapper.IdentityDtoMapper;
import com.kaditsm.auth.application.port.in.CreateIdentityUseCase;
import com.kaditsm.auth.application.port.in.DeactivateIdentityUseCase;
import com.kaditsm.auth.application.port.in.GetIdentitiesUseCase;
import com.kaditsm.auth.application.port.in.UpdateIdentityUseCase;
import com.kaditsm.auth.application.port.in.CreateIdentityUseCase.CreateIdentityCommand;
import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.PagedResult;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.kaditsm.auth.config.security.UserPrincipal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/identities")
public class IdentityController {

    private final UpdateIdentityUseCase updateIdentityUseCase;
    private final DeactivateIdentityUseCase deactivateIdentityUseCase;
    private final IdentityDtoMapper identityDtoMapper;
    private final CreateIdentityUseCase createIdentityUseCase;
    private final GetIdentitiesUseCase getIdentitiesUseCase;

    public IdentityController(
            UpdateIdentityUseCase updateIdentityUseCase,
            DeactivateIdentityUseCase deactivateIdentityUseCase,
            IdentityDtoMapper identityDtoMapper,
            CreateIdentityUseCase createIdentityUseCase,
            GetIdentitiesUseCase getIdentitiesUseCase) {
        this.updateIdentityUseCase = updateIdentityUseCase;
        this.deactivateIdentityUseCase = deactivateIdentityUseCase;
        this.identityDtoMapper = identityDtoMapper;
        this.createIdentityUseCase = createIdentityUseCase;
        this.getIdentitiesUseCase = getIdentitiesUseCase;
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdentityResponse> getById(@PathVariable UUID id) {
        Identity identity = getIdentitiesUseCase.getIdentityById(id);
        return ResponseEntity.ok(identityDtoMapper.toResponse(identity));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<IdentityResponse>> getAll(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Identity identity = getIdentitiesUseCase.getIdentityById(principal.identityId());

        PagedResult<Identity> result = getIdentitiesUseCase.getIdentities(identity.getTenantId(), page, size);

        List<IdentityResponse> content = result.content().stream()
                .map(identityDtoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(new PagedResponse<>(
                content,
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()));
    }

    @PostMapping
    public ResponseEntity<IdentityResponse> createIdentity(@Valid @RequestBody CreateIdentityRequest request) {
        Identity identity = createIdentityUseCase.create(
                new CreateIdentityCommand(
                        request.tenantId(),
                        request.email(),
                        request.password()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(identityDtoMapper.toResponse(identity));

    }

    @PatchMapping("/{id}")
    public ResponseEntity<IdentityResponse> updateIdentity(
            @PathVariable("id") UUID accountId,
            @Valid @RequestBody UpdateIdentity request) {

        UpdateIdentityUseCase.UpdateIdentityCommand command = identityDtoMapper.toCommand(accountId, request);
        Identity updatedAccount = updateIdentityUseCase.updateIdentity(command);
        return ResponseEntity.ok(identityDtoMapper.toResponse(updatedAccount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateIdentity(@PathVariable("id") UUID accountId) {
        deactivateIdentityUseCase.deactivate(accountId);
        return ResponseEntity.noContent().build();
    }
}