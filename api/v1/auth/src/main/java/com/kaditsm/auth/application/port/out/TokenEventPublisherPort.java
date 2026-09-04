package com.kaditsm.auth.application.port.out;

import com.kaditsm.auth.domain.event.TokenBlacklistedEvent;

public interface TokenEventPublisherPort {
    void publishTokenBlacklisted(TokenBlacklistedEvent event);
}