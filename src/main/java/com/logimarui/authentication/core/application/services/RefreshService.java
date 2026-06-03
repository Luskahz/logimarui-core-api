package com.logimarui.authentication.core.application.services;

import com.logimarui.authentication.core.application.exceptions.refreshtoken.RFInvalidException;
import com.logimarui.authentication.core.application.exceptions.refreshtoken.RFNotFoundException;
import com.logimarui.authentication.core.application.results.AuthTokens;
import com.logimarui.authentication.core.domain.model.IssuedAccessToken;
import com.logimarui.authentication.core.domain.model.RefreshToken;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.port.ApplicationTimeProperties;
import com.logimarui.authentication.core.port.JwtService;
import com.logimarui.authentication.core.port.RefreshTokenGenerator;
import com.logimarui.authentication.core.port.RefreshTokenHashService;
import com.logimarui.authentication.core.repository.RefreshTokenRepository;
import com.logimarui.authentication.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenHashService tokenHashService;
    private final RefreshTokenGenerator tokenGenerator;
    private final JwtService jwtService;
    private final ApplicationTimeProperties authTimeProperties;

    @Transactional
    public AuthTokens refresh(String rawRefreshToken) {
        Instant now = Instant.now();

        String tokenHash = tokenHashService.hash(rawRefreshToken);

        RefreshToken existingToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new RFNotFoundException("Refresh token not found."));

        if (!existingToken.isValid(now)) {
            throw new RFInvalidException("Invalid refresh token for rotation rules");
        }

        User user = userRepository.findById(existingToken.getSession().getUserId())
                .orElseThrow(() -> new SecurityException("User not found with this token."));

        String newRawToken = tokenGenerator.generate();
        String newTokenHash = tokenHashService.hash(newRawToken);

        RefreshToken newToken = RefreshToken.create(
                existingToken.getSession(),
                newTokenHash,
                authTimeProperties.refreshTokenTtl()
        );
        RefreshToken savedNewToken = refreshTokenRepository.save(newToken, existingToken.getSession());
        existingToken.rotate(savedNewToken.getId(), now);
        refreshTokenRepository.save(existingToken, existingToken.getSession());

        IssuedAccessToken issuedAccessToken = jwtService.generateAccessToken(user, existingToken.getSession());
        long expiresInSeconds = Math.max(
                Duration.between(now, issuedAccessToken.expiresAt()).getSeconds(), 0
        );

        return new AuthTokens(newRawToken, issuedAccessToken.token(), expiresInSeconds);
    }
}