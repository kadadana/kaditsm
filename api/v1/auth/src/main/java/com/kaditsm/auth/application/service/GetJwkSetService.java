package com.kaditsm.auth.application.service;

import org.springframework.stereotype.Service;

import com.kaditsm.auth.application.port.in.GetJwkSetUseCase;
import com.kaditsm.auth.application.port.out.JwksKeyProviderPort;

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