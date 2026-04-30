package com.logimarui.authentication.infra.security.jwt;

import com.logimarui.authentication.core.domain.enums.Role;
import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.domain.model.IssuedAccessToken;
import com.logimarui.authentication.core.port.JwtService;
import com.logimarui.infra.security.jwt.JwtProperties;
import com.logimarui.infra.security.jwt.JwtSigningKeyProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JwtProperties jwtProperties;
    private final JwtSigningKeyProvider keyProvider;

    @Override
    public IssuedAccessToken generateAccessToken(@NotNull User user, @NotNull Session session) {
        Instant now = Instant.now();
        Instant expiresAt =
                now.plusSeconds(jwtProperties.getAccessTokenExpirationSeconds());

        String token = Jwts.builder()
                .setIssuer(jwtProperties.getIssuer())
                .setSubject(user.getId().toString())
                .claim("sid", session.getId())
                .claim(
                        "roles",
                        user.getRoles().stream().map(Role::name).toList()
                )
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(keyProvider.getKey(), SignatureAlgorithm.HS256)
                .compact();

        return new IssuedAccessToken(token, expiresAt);
    }
}

