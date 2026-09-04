package com.kaditsm.auth.adapter.in.web.jwks;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kaditsm.auth.application.port.in.GetJwkSetUseCase;

import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwksController {

    private final GetJwkSetUseCase getJwkSetUseCase;

    public JwksController(GetJwkSetUseCase getJwkSetUseCase) {
        this.getJwkSetUseCase = getJwkSetUseCase;
    }

    @GetMapping(value = "/jwks.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getJwks() {
        return getJwkSetUseCase.getJwkSet();
    }
}