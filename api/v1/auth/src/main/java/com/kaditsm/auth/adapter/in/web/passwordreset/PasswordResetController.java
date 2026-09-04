package com.kaditsm.auth.adapter.in.web.passwordreset;

import com.kaditsm.auth.adapter.in.web.passwordreset.dto.CompletePasswordResetRequest;
import com.kaditsm.auth.adapter.in.web.passwordreset.dto.CreatePasswordResetTokenRequest;
import com.kaditsm.auth.adapter.in.web.passwordreset.mapper.PasswordResetDtoMapper;
import com.kaditsm.auth.application.port.in.CompletePasswordResetUseCase;
import com.kaditsm.auth.application.port.in.RequestPasswordResetUseCase;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/password-reset-tokens")
public class PasswordResetController {

    private final RequestPasswordResetUseCase requestPasswordResetUseCase;
    private final CompletePasswordResetUseCase completePasswordResetUseCase;
    private final PasswordResetDtoMapper passwordResetDtoMapper;

    public PasswordResetController(RequestPasswordResetUseCase requestPasswordResetUseCase,
            CompletePasswordResetUseCase completePasswordResetUseCase,
            PasswordResetDtoMapper passwordResetDtoMapper) {
        this.requestPasswordResetUseCase = requestPasswordResetUseCase;
        this.completePasswordResetUseCase = completePasswordResetUseCase;
        this.passwordResetDtoMapper = passwordResetDtoMapper;
    }

    @PostMapping
    public ResponseEntity<Void> requestReset(@Valid @RequestBody CreatePasswordResetTokenRequest request) {
        RequestPasswordResetUseCase.RequestPasswordResetCommand command = passwordResetDtoMapper.toCommand(request);
        requestPasswordResetUseCase.requestReset(command);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> completeReset(
            @PathVariable("id") String tokenId,
            @Valid @RequestBody CompletePasswordResetRequest request) {

        CompletePasswordResetUseCase.CompletePasswordResetCommand command = passwordResetDtoMapper.toCommand(tokenId,
                request);
        completePasswordResetUseCase.completeReset(command);
        return ResponseEntity.noContent().build();
    }
}