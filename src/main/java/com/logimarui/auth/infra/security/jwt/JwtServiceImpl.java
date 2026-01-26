package com.logimarui.auth.infra.security.jwt;

import com.logimarui.auth.core.domain.enums.Role;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.domain.model.IssuedAccessToken;
import com.logimarui.auth.core.port.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtProperties jwtProperties;
    private Key signingKey;

    @PostConstruct
    void init() {
        this.signingKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }
    @Override
    public IssuedAccessToken generateAccessToken(@NotNull User user, @NotNull Session session) {
        Instant now = Instant.now();
        Instant expiresAt =
                now.plusSeconds(jwtProperties.getAccessTokenExpirationSeconds());

        String token = Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("sid", session.getId())
                .claim(
                        "roles",
                        user.getRoles().stream().map(Role::name).toList()
                )
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();

        return new IssuedAccessToken(token, expiresAt);
    }
}

