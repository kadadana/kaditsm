package com.kaditsm.auth.application.service;

import com.kaditsm.auth.domain.port.in.RequestPasswordResetUseCase;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
@Transactional
public class RequestPasswordResetService implements RequestPasswordResetUseCase {

    @Override
    public void requestReset(RequestPasswordResetCommand command) {
        // TODO: implement here
    }
}