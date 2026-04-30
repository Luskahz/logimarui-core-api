package com.logimarui.authentication.core.application.services;

import com.logimarui.authentication.core.domain.enums.RefreshTokenStatus;
import com.logimarui.authentication.core.domain.enums.Role;
import com.logimarui.authentication.core.domain.enums.SessionStatus;
import com.logimarui.authentication.core.domain.exception.UserNotFoundException;
import com.logimarui.authentication.core.domain.model.PasswordChangeRequest;
import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.port.*;
import com.logimarui.authentication.core.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RecoverPasswordService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordChangeRequestRepository passwordChangeRequestRepository;
    private final AuthTimeProperties authTokenProperties;
    private final PasswordHasher passwordHasher;
    private final UtilsServices utilsServices;

    @Transactional
    public PasswordChangeRequest forgotPassword(String cpf, String ip, String deviceId){
        Instant now = Instant.now();
        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() -> new UserNotFoundException("User do not exist"));

        user.assertCanRequestPasswordChange();

        Optional<PasswordChangeRequest> existing =
                passwordChangeRequestRepository.findActiveByUserId(user.getId());

        if (existing.isPresent()) {
            return existing.get();
        }

        sessionRepository
                .findByUserIdAndDeviceIdAndSessionStatus(user.getId(), deviceId, SessionStatus.ACTIVE)
                .ifPresent(session -> {
                    utilsServices.logoutSession(session, now);

                    utilsServices.findActiveRefreshTokenBySession(session)
                            .ifPresent(token -> {
                                token.revoke();
                                refreshTokenRepository.save(token, session);
                            });
                });

        PasswordChangeRequest newRequest =
                PasswordChangeRequest.create(
                        user.getId(),
                        ip,
                        deviceId,
                        authTokenProperties.passwordChangeRequestTtl()
                );

        return passwordChangeRequestRepository.save(newRequest);
    }


    @Transactional public void changePassword(String cpf, String deviceId, Long passwordChangeRequestId, String newPassword) {
        Instant now = Instant.now();
        PasswordChangeRequest request =
                passwordChangeRequestRepository.findById(passwordChangeRequestId)
                        .orElseThrow(() ->
                                new IllegalStateException("Password change request not found")
                        );

        validatePasswordChangeRequest(request, deviceId, now);
        User user = userRepository.findByCpf(cpf)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found for request")
                );
        if (!Objects.equals(request.getUserId(), user.getId())) {
            throw new IllegalStateException("Request does not belong to this user");
        }
        if (!user.isActive()) {
            throw new IllegalStateException("User cannot authenticate");
        }
        user.changePassword(
                passwordHasher.hash(newPassword),
                now
        );
        userRepository.save(user);
        invalidateAllUserSessions(user, now);
        request.complete(now);
        passwordChangeRequestRepository.save(request);
    }

    @Transactional public void authorizeChangePassword(Long authorizerId, Long requestId) {
        Instant now = Instant.now();

        User authorizer = userRepository.findById(authorizerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!authorizer.getRoles().contains(Role.ADMINISTRATIVO)
                && !authorizer.getRoles().contains(Role.ADMINISTRADOR)) {
            throw new AccessDeniedException("User not allowed to authorize password change");
        }

        PasswordChangeRequest request = passwordChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.canBeResolved(now)) {
            throw new IllegalStateException("Password change request cannot be authorized");
        }

        request.authorize(authorizerId, now);
        passwordChangeRequestRepository.save(request);
    }

    private void validatePasswordChangeRequest(@NotNull PasswordChangeRequest request, String deviceId, Instant now) {
        if (!request.isRequestedFromDevice(deviceId)) {
            throw new IllegalStateException("This request is from another device");
        }
        if (!request.canChangePassword(now)) {
            throw new IllegalStateException("Password change not allowed");
        }
    }

    private void invalidateAllUserSessions(@NotNull User user, Instant now) {
        List<Session> sessions =
                sessionRepository.findByUserIdAndSessionStatus(
                        user.getId(),
                        SessionStatus.ACTIVE
                );

        for (Session session : sessions) {
            refreshTokenRepository
                    .findBySessionIdAndRefreshTokenStatus(
                            session.getId(),
                            RefreshTokenStatus.ACTIVE
                    )
                    .ifPresent(token -> {
                        token.revoke();
                        refreshTokenRepository.save(token, session);
                    });

            utilsServices.logoutSession(session,now);
        }
    }
}
