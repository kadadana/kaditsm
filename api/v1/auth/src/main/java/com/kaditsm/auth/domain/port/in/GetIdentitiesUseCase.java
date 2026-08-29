package com.kaditsm.auth.domain.port.in;

import java.util.UUID;

import com.kaditsm.auth.domain.model.Identity;
import com.kaditsm.auth.domain.model.PagedResult;


public interface GetIdentitiesUseCase {

    PagedResult<Identity> getIdentities(UUID tenantId, int page, int size);

    Identity getIdentityById(UUID id);

}
