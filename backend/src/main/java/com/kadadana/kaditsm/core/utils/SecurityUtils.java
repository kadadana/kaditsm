package com.kadadana.kaditsm.core.utils;

import org.springframework.security.core.Authentication;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Authentication nesnesinden kullanıcının id (Principal) bilgisini çeker.
     */
    public static String getId(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new org.springframework.security.authentication.BadCredentialsException("Oturum bilgisi bulunamadı.");
        }
        return (String) authentication.getPrincipal();
    }

    /**
     * Spring Security rollerinin başındaki "ROLE_" ekini temizleyerek ham string
     * döner.
     */
    public static String getUserRole(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return "KULLANICI";
        }
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .orElse("KULLANICI");
    }
}