package com.logimarui.authentication.core.application.services;

import com.logimarui.authentication.core.application.exceptions.tokenhash.TokenInvalidException;
import com.logimarui.authentication.core.application.exceptions.tokenhash.TokenNotFoundException;
import com.logimarui.authentication.core.application.exceptions.user.UserIdNotFoundException;
import com.logimarui.authentication.core.application.exceptions.user.UserInvalidCredentialsException;
import com.logimarui.authentication.core.application.results.PasswordResetLinkAdminResult;
import com.logimarui.authentication.core.application.results.TemporaryPasswordAdminResult;
import com.logimarui.authentication.core.domain.model.PasswordRecoveryRequest;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.port.*;
import com.logimarui.authentication.core.repository.PasswordRecoveryRequestRepository;
import com.logimarui.authentication.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final UserRepository userRepository;
    private final PasswordRecoveryRequestRepository passwordRecoveryRequestRepository;
    private final NotificationPort notificationPort;
    private final RandomPasswordGenerator randomPasswordGenerator;
    private final RefreshTokenGenerator tokenGenerator;
    private final RefreshTokenHashService tokenHashService;
    private final PasswordHasher passwordHasher;
    private final ApplicationTimeProperties authTimeProperties;
    private final ApplicationUrlProperties applicationUrlProperties;
    private final Clock clock;

    @Transactional
    public PasswordRecoveryRequest createOrGetOpenRecoveryRequest(String cpf) {
        Instant now = Instant.now(clock);

        User user = userRepository.findByCpf(cpf)
                .orElseThrow(UserInvalidCredentialsException::new);

        user.assertCanRequestPasswordChange();

        return getOrCreateOpenRecoveryRequest(user.getId(), now);
    }

    @Transactional
    public PasswordRecoveryRequest sendRecoveryEmailToken(String cpf) {
        Instant now = Instant.now(clock);

        User user = userRepository.findByCpf(cpf)
                .orElseThrow(UserInvalidCredentialsException::new);

        user.assertCanRequestPasswordChange();

        PasswordRecoveryRequest recoveryRequest =
                getOrCreateOpenRecoveryRequest(user.getId(), now);

        String rawToken = tokenGenerator.generate();
        String hashedToken = tokenHashService.hash(rawToken);

        recoveryRequest.changeToEmailTokenMethod(hashedToken, now);

        PasswordRecoveryRequest savedRecoveryRequest =
                passwordRecoveryRequestRepository.save(recoveryRequest);

        notificationPort.sendPasswordResetLink(
                user.getEmail(),
                buildResetLink(rawToken)
        );

        return savedRecoveryRequest;
    }

    @Transactional
    public void resetPasswordByToken(String rawToken, String newPassword) {
        Instant now = Instant.now(clock);

        String hashedToken = tokenHashService.hash(rawToken);

        PasswordRecoveryRequest recoveryRequest =
                passwordRecoveryRequestRepository.findByTokenHash(hashedToken)
                        .orElseThrow(() -> new TokenNotFoundException("Token invalid or expired."));

        if (!recoveryRequest.isTokenBased()) {
            throw new TokenInvalidException("Token invalid or expired.");
        }

        if (!recoveryRequest.isOpen(now)) {
            throw new TokenInvalidException("Token invalid or expired.");
        }

        User user = userRepository.findById(recoveryRequest.getUserId())
                .orElseThrow(() -> new UserIdNotFoundException("User not found with this id."));

        String newPasswordHash = passwordHasher.hash(newPassword);

        user.changePassword(newPasswordHash, now);
        recoveryRequest.resolve(now);

        userRepository.save(user);
        passwordRecoveryRequestRepository.save(recoveryRequest);
    }

    @Transactional
    public TemporaryPasswordAdminResult generateTemporaryPasswordByAdmin(Long targetUserId) {
        Instant now = Instant.now(clock);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserIdNotFoundException("User target not found."));

        user.assertCanRequestPasswordChange();

        PasswordRecoveryRequest recoveryRequest =
                getOrCreateOpenRecoveryRequest(user.getId(), now);

        recoveryRequest.changeToAdminTemporaryPasswordMethod(now);

        String temporaryPassword = randomPasswordGenerator.generateSecurePassword();
        String temporaryPasswordHash = passwordHasher.hash(temporaryPassword);

        user.changeToTemporaryPassword(temporaryPasswordHash, now);

        userRepository.save(user);
        PasswordRecoveryRequest savedRecoveryRequest =
                passwordRecoveryRequestRepository.save(recoveryRequest);

        return new TemporaryPasswordAdminResult(
                temporaryPassword,
                savedRecoveryRequest
        );
    }

    @Transactional
    public PasswordResetLinkAdminResult generateResetLinkByAdmin(Long targetUserId) {
        Instant now = Instant.now(clock);

        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new UserIdNotFoundException("User target not found."));

        user.assertCanRequestPasswordChange();

        PasswordRecoveryRequest recoveryRequest =
                getOrCreateOpenRecoveryRequest(user.getId(), now);

        String rawToken = tokenGenerator.generate();
        String hashedToken = tokenHashService.hash(rawToken);

        recoveryRequest.changeToAdminResetLinkMethod(hashedToken, now);

        PasswordRecoveryRequest savedRecoveryRequest =
                passwordRecoveryRequestRepository.save(recoveryRequest);

        return new PasswordResetLinkAdminResult(
                buildResetLink(rawToken),
                savedRecoveryRequest
        );
    }

    private PasswordRecoveryRequest getOrCreateOpenRecoveryRequest(
            Long userId,
            Instant now
    ) {
        return passwordRecoveryRequestRepository
                .findOpenByUserId(userId, now)
                .orElseGet(() -> createOpenRecoveryRequest(userId, now));
    }

    private PasswordRecoveryRequest createOpenRecoveryRequest(
            Long userId,
            Instant now
    ) {
        Instant expiresAt = now.plus(authTimeProperties.passwordRecoveryRequestTtl());

        PasswordRecoveryRequest recoveryRequest =
                PasswordRecoveryRequest.createOpenRequest(
                        userId,
                        now,
                        expiresAt
                );

        return passwordRecoveryRequestRepository.save(recoveryRequest);
    }

    private String buildResetLink(String rawToken) {
        return applicationUrlProperties.baseUrl()
                + "/password-recovery/reset?token="
                + rawToken;
    }
}