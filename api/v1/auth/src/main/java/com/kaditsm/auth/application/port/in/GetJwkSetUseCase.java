package com.kaditsm.auth.application.port.in;

import java.util.Map;

public interface GetJwkSetUseCase {
    Map<String, Object> getJwkSet();
}