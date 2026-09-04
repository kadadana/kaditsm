package com.kaditsm.auth.application.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.kaditsm.auth.application.port.in.RequestPasswordResetUseCase;

@Service
@Transactional
public class RequestPasswordResetService implements RequestPasswordResetUseCase {

    @Override
    public void requestReset(RequestPasswordResetCommand command) {
        // TODO: implement here
    }
}