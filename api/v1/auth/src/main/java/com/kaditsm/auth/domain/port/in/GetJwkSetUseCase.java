package com.kaditsm.auth.domain.port.in;

import java.util.Map;

public interface GetJwkSetUseCase {
    Map<String, Object> getJwkSet();
}