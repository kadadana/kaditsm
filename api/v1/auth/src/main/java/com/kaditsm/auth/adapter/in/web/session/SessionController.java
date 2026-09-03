package com.kaditsm.auth.adapter.in.web.session;

import com.kaditsm.auth.adapter.in.web.session.dto.CreateSessionRequest;
import com.kaditsm.auth.adapter.in.web.session.dto.SessionResponse;
import com.kaditsm.auth.adapter.in.web.session.mapper.SessionDtoMapper;
import com.kaditsm.auth.domain.model.LoginResult;
import com.kaditsm.auth.domain.port.in.CreateSessionUseCase;
import com.kaditsm.auth.domain.port.in.CreateTokenUseCase;
import com.kaditsm.auth.domain.port.in.TerminateSessionUseCase;
import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sessions")
public class SessionController {

    private final CreateSessionUseCase createSessionUseCase;
    private final TerminateSessionUseCase terminateSessionUseCase;
    private final SessionDtoMapper sessionDtoMapper;
    private final CreateTokenUseCase createTokenUseCase;

    public SessionController(CreateSessionUseCase createSessionUseCase,
            TerminateSessionUseCase terminateSessionUseCase,
            SessionDtoMapper sessionDtoMapper,
            CreateTokenUseCase createTokenUseCase) {
        this.createSessionUseCase = createSessionUseCase;
        this.terminateSessionUseCase = terminateSessionUseCase;
        this.sessionDtoMapper = sessionDtoMapper;
        this.createTokenUseCase = createTokenUseCase;
    }

    @PostMapping("/tokens")
    public ResponseEntity<SessionResponse> createToken(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {

        String refreshToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            refreshToken = authHeader.substring(7);
        }

        CreateTokenUseCase.CreateTokenCommand command = new CreateTokenUseCase.CreateTokenCommand(refreshToken);

        LoginResult loginResult = createTokenUseCase.createToken(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionDtoMapper.toResponse(loginResult));
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        CreateSessionUseCase.CreateSessionCommand command = sessionDtoMapper.toCommand(request);
        LoginResult authToken = createSessionUseCase.createSession(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionDtoMapper.toResponse(authToken));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> terminateSession(
            @PathVariable("id") UUID jti,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = true) String authHeader) {

        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        terminateSessionUseCase.terminateSession(jti, accessToken);
        return ResponseEntity.noContent().build();
    }
}