package com.logimarui.infra.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;



@Component
@RequiredArgsConstructor
public class JwtTokenParser {

    private final JwtProperties jwtProperties;
    private final JwtSigningKeyProvider keyProvider;


    public JwtAuthenticatedUser parseAndValidate(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(keyProvider.getKey())
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = Long.valueOf(claims.getSubject());

        Number sidNum = claims.get("sid", Number.class);
        Long sessionId = sidNum != null ? sidNum.longValue() : null;

        Object rawRoles = claims.get("roles");
        List<String> roles = (rawRoles instanceof List<?> list)
                ? list.stream().map(String::valueOf).toList()
                : List.of();

        Instant expiresAt = claims.getExpiration().toInstant();

        return new JwtAuthenticatedUser(userId, sessionId, roles, expiresAt);
    }
}

