package com.kaditsm.auth.domain.port.out;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

public interface JwksKeyProviderPort {
    String getKeyId();

    RSAPrivateKey getPrivateKey();

    RSAPublicKey getPublicKey();

    Map<String, Object> getPublicJwkSet();
}