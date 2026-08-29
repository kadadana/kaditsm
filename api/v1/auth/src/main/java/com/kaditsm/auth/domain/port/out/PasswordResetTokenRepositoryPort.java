package com.kaditsm.auth.domain.port.out;

import com.kaditsm.auth.domain.model.PasswordResetToken;
import java.util.Optional;

public interface PasswordResetTokenRepositoryPort {
    void save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByToken(String token);
}