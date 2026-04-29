package com.logimarui.auth.core.application.services;

import com.logimarui.auth.core.application.results.AuthTokens;
import com.logimarui.auth.core.application.results.IssuedRefreshToken;
import com.logimarui.auth.core.domain.model.IssuedAccessToken;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.port.*;
import com.logimarui.auth.core.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordChangeRequestRepository passwordChangeRequestRepository;
    private final AuthTimeProperties authTokenProperties;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;
    private final EmployeeRepository employeeRepository;
    private final UtilsServices utilsServices;


    @Transactional
    public AuthTokens refresh(String refreshToken, String ip, String deviceId){
        Instant now = Instant.now();
        RefreshToken existingToken = findRefreshTokenByToken(refreshToken)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));
        if (!existingToken.isValid(now)) {
            throw new SecurityException("Refresh token invalid");
        }

        User user = userRepository.findById(existingToken.getSession().getUserId())
                .orElseThrow(() -> new SecurityException("User not found in session"));

        if (!Objects.equals(existingToken.getSession().getDeviceId(), deviceId)) {
            throw new SecurityException("Different deviceId during refresh");
        }

        utilsServices.updateSessionLastIpAddressIfChanged(existingToken.getSession(),ip, now);
        IssuedRefreshToken issuedRefreshToken = rotateRefreshTokenFromExisting(existingToken, now);
        IssuedAccessToken issuedAccessToken = jwtService.generateAccessToken(user, issuedRefreshToken.refreshToken().getSession());
        long expiresInSeconds = Math.max(Duration.between(now, issuedAccessToken.expiresAt()).getSeconds(), 0);
        return new AuthTokens(
                issuedRefreshToken.rawToken(),
                issuedAccessToken.token(),
                expiresInSeconds
        );

    }

    private @NotNull IssuedRefreshToken rotateRefreshTokenFromExisting(@NotNull RefreshToken refreshToken, Instant now){
        String raw = tokenGenerator.generate();
        String hash = tokenHashService.hash(raw);
        refreshToken.rotate(hash, authTokenProperties.refreshTokenTtl(), now);
        refreshTokenRepository.save(refreshToken, refreshToken.getSession());

        return new IssuedRefreshToken(
                refreshToken,
                raw
        );
    }

    public Optional<RefreshToken> findRefreshTokenByToken(@NotNull String refreshToken){
        String tokenHash = tokenHashService.hash(refreshToken);
        return refreshTokenRepository.findByTokenHash(tokenHash);

    }
}
