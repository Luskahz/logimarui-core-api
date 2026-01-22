package com.logimarui.auth.infra.security.jwt;

import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService  {
    private final JwtProperties jwtProperties;
    private Key signingKey;

    @PostConstruct
    void init() {
        this.signingKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String generateAccessToken(User user, Session session) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.getAccessTokenExpirationSeconds());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("sid", session.getId())
                .claim("roles", List.of(user.getRole().name()))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(signingKey)
                .compact();
    }

    @Override
    public long getAccessTokenExpiresInSeconds() {
        return jwtProperties.getAccessTokenExpirationSeconds();
    }
}
