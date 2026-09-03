package com.kaditsm.auth.adapter.in.web.jwks;

import com.kaditsm.auth.domain.port.in.GetJwkSetUseCase;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JwksController {

    private final GetJwkSetUseCase getJwkSetUseCase;

    public JwksController(GetJwkSetUseCase getJwkSetUseCase) {
        this.getJwkSetUseCase = getJwkSetUseCase;
    }

    @GetMapping(value = "/.well-known/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getJwks() {
        return getJwkSetUseCase.getJwkSet();
    }
}