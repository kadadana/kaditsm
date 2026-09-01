package com.kaditsm.auth.config.security;

import java.util.UUID;

public record UserPrincipal(
    UUID identityId

) {
}
