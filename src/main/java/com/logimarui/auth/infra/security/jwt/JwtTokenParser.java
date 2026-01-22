package com.logimarui.auth.infra.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.List;



@Component
@RequiredArgsConstructor
public class JwtTokenParser {

    private final JwtProperties jwtProperties;
    private Key signingKey;

    @PostConstruct
    void init() {
        this.signingKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }


    public JwtAuthenticatedUser parseAndValidate(String token) {

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Long userId = Long.valueOf(claims.getSubject());
        Long sessionId = claims.get("sid", Long.class);
        List<String> roles = claims.get("roles", List.class);
        Instant expiresAt = claims.getExpiration().toInstant();

        return new JwtAuthenticatedUser(
                userId,
                sessionId,
                roles,
                expiresAt
        );
    }
}

