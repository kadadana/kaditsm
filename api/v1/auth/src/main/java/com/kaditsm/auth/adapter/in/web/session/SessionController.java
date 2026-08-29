package com.kaditsm.auth.adapter.in.web.session;

import com.kaditsm.auth.adapter.in.web.session.dto.CreateSessionRequest;
import com.kaditsm.auth.adapter.in.web.session.dto.SessionResponse;
import com.kaditsm.auth.adapter.in.web.session.mapper.SessionDtoMapper;
import com.kaditsm.auth.domain.model.AuthToken;
import com.kaditsm.auth.domain.port.in.CreateSessionUseCase;
import com.kaditsm.auth.domain.port.in.TerminateSessionUseCase;
import jakarta.validation.Valid;
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

    public SessionController(CreateSessionUseCase createSessionUseCase,
            TerminateSessionUseCase terminateSessionUseCase,
            SessionDtoMapper sessionDtoMapper) {
        this.createSessionUseCase = createSessionUseCase;
        this.terminateSessionUseCase = terminateSessionUseCase;
        this.sessionDtoMapper = sessionDtoMapper;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        CreateSessionUseCase.CreateSessionCommand command = sessionDtoMapper.toCommand(request);
        AuthToken authToken = createSessionUseCase.createSession(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(sessionDtoMapper.toResponse(authToken));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> terminateSession(
            @PathVariable("id") String sessionId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {

        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        terminateSessionUseCase.terminateSession(sessionId, accessToken);
        return ResponseEntity.noContent().build();
    }
}