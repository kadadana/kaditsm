package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.port.in.GetJwkSetUseCase;
import com.kaditsm.auth.domain.port.out.JwksKeyProviderPort;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class GetJwkSetService implements GetJwkSetUseCase {

    private final JwksKeyProviderPort jwkKeyProviderPort;

    public GetJwkSetService(JwksKeyProviderPort jwkKeyProviderPort) {
        this.jwkKeyProviderPort = jwkKeyProviderPort;
    }

    @Override
    public Map<String, Object> getJwkSet() {
        return jwkKeyProviderPort.getPublicJwkSet();
    }
}