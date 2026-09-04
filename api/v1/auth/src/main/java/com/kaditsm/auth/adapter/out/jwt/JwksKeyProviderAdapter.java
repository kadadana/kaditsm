package com.kaditsm.auth.adapter.out.jwt;

import com.kaditsm.auth.application.port.out.JwksKeyProviderPort;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;

import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Component
public class JwksKeyProviderAdapter implements JwksKeyProviderPort {

    private final RSAKey rsaJwk;

    public JwksKeyProviderAdapter(
            @Value("${jwt.private-key}") String privateKeyPem,
            @Value("${jwt.key-id}") String keyId) {
        this.rsaJwk = buildRsaJwk(keyId, privateKeyPem);
    }

    private RSAKey buildRsaJwk(String keyId, String pem) {
        try {
            String cleanKey = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey) keyFactory.generatePrivate(spec);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(
                    privateKey.getModulus(),
                    privateKey.getPublicExponent());
            RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);

            return new RSAKey.Builder(publicKey)
                    .privateKey(privateKey)
                    .keyID(keyId)
                    .build();

        } catch (Exception e) {
            throw new IllegalStateException("Failed to load RSA key from configuration", e);
        }
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        try {
            return rsaJwk.toRSAPrivateKey();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract RSA private key", e);
        }
    }

    @Override
    public RSAPublicKey getPublicKey() {
        try {
            return rsaJwk.toRSAPublicKey();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract RSA public key", e);
        }
    }

    @Override
    public String getKeyId() {
        return rsaJwk.getKeyID();
    }

    @Override
    public Map<String, Object> getPublicJwkSet() {
        return new JWKSet(rsaJwk.toPublicJWK()).toJSONObject();
    }
}