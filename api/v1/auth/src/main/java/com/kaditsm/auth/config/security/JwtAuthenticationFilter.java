package com.kaditsm.auth.config.security;

import com.kaditsm.auth.adapter.out.jwt.JwtTokenProviderAdapter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProviderAdapter jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProviderAdapter jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Claims claims = jwtTokenProvider.parseClaims(token);

                UUID accountId = UUID.fromString(claims.getSubject());
                UUID tenantId = UUID.fromString(claims.get("tenant_id", String.class));
                String email = claims.get("email", String.class);

                UserPrincipal principal = new UserPrincipal(accountId, tenantId, email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(principal,
                        null, Collections.emptyList());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }
}