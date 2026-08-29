package com.kaditsm.auth.application.service;

import org.springframework.stereotype.Service;

import com.kaditsm.auth.domain.port.in.CompletePasswordResetUseCase;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CompletePasswordResetService implements CompletePasswordResetUseCase {

    @Override
    public void completeReset(CompletePasswordResetCommand command) {
        // TODO
        // Implement the logic to complete the password reset process
        // This may include validating the token, updating the user's password, etc.
    }

}
