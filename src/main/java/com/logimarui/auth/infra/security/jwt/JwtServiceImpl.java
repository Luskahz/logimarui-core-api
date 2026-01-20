package com.logimarui.auth.infra.security.jwt;

import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtServiceImpl implements JwtService {
    private static final long ACCESS_TOKEN_EXP_SECONDS = 15 * 60;
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Override
    public String generateAccessToken(User user, Session session) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ACCESS_TOKEN_EXP_SECONDS);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("sid", session.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    @Override
    public long getAccessTokenExpiresInSeconds() {
        return ACCESS_TOKEN_EXP_SECONDS;
    }
}
