package com.logimarui.auth.core.service;

import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import com.logimarui.auth.core.domain.enums.Role;
import com.logimarui.auth.core.domain.enums.SessionStatus;
import com.logimarui.auth.core.domain.exception.UserNotFoundException;
import com.logimarui.auth.core.domain.model.*;
import com.logimarui.auth.core.port.*;
import com.logimarui.auth.core.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuthService {
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


    @Transactional public AuthTokens login(@NotNull Long employeeId, String password, String ip, String deviceId) {
        Instant now = Instant.now();
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                    new UserNotFoundException("User not found for provided employee number")
                );
        user.assertCanAuthenticate();

        if (!passwordHasher.matches(password, user.getPasswordHash())) {
            user.registerFailedLogin();
            userRepository.save(user);
            throw new SecurityException("wrong password");
        }

        Session session = findSessionByUserAndDeviceIdAndSessionStatus(user, deviceId, SessionStatus.ACTIVE)
                .map(existingSession -> {
                    if(existingSession.isValid(now)){
                        existingSession.updateIpAddress(ip, now);
                        sessionRepository.save(existingSession);
                        return existingSession;
                    }
                    return sessionRegister(
                            user, deviceId, ip
                    );
                })
                .orElseGet(() -> sessionRegister(user, deviceId, ip));



        IssuedRefreshToken issuedRefreshToken = refreshTokenRegister(session);
        IssuedAccessToken issuedAccessToken = jwtService.generateAccessToken(user, session);
        long expiresInSeconds = Math.max(Duration.between(now, issuedAccessToken.expiresAt()).getSeconds(), 0);

        user.recordSuccessfulLogin(now);
        userRepository.save(user);
        return new AuthTokens(
                issuedRefreshToken.rawToken(),
                issuedAccessToken.token(),
                expiresInSeconds
        );
    }
    public User registerUser(String username, Long employeeId, String password, String ip){
        if(!userCanBeCreated(employeeId)) throw new IllegalStateException("the Employee is not available to an new user");
        return userRegister(username, employeeId, password, ip);
    }

    @Transactional public AuthContext me(
            @NotNull Long userId,
            @NotNull Long sessionId,
            Instant accessTokenExpiresAt,
            String ip,
            String deviceId
    ) {
        Instant now = Instant.now();

        userRepository.findById(userId)
                .map(User::assertCanAuthenticate)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        Session session = findSessionById(sessionId)
                .orElseThrow(()-> new SecurityException("Session not found"));
        if (!Objects.equals(session.getUserId(), userId)
                || !Objects.equals(session.getDeviceId(), deviceId)) {
            throw new SecurityException("Session does not belong to this user/device");
        }

        updateSessionLastIpAddressIfChanged(session, ip, now);

        return new AuthContext (
                userId,
                sessionId,
                session.isValid(now),
                getAccessTokenRemainingSeconds(now, accessTokenExpiresAt)
        );
    }
    @Transactional public AuthTokens refresh(String refreshToken, String ip, String deviceId){
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

        updateSessionLastIpAddressIfChanged(existingToken.getSession(),ip, now);
        IssuedRefreshToken issuedRefreshToken = rotateRefreshTokenFromExisting(existingToken, now);
        IssuedAccessToken issuedAccessToken = jwtService.generateAccessToken(user, issuedRefreshToken.refreshToken().getSession());
        long expiresInSeconds = Math.max(Duration.between(now, issuedAccessToken.expiresAt()).getSeconds(), 0);
        return new AuthTokens(
                issuedRefreshToken.rawToken(),
                issuedAccessToken.token(),
                expiresInSeconds
        );
    }
    @Transactional public void logout(Long userId, String ip, String deviceId) {
        Instant now = Instant.now();

        Optional<Session> sessionOpt =
                sessionRepository.findByUserIdAndDeviceIdAndSessionStatus(userId, deviceId, SessionStatus.ACTIVE);

        if (sessionOpt.isEmpty()) {
            return;
        }

        Session session = sessionOpt.get();

        if (!session.isLoggedOut()) {
            session.updateIpAddress(ip, now);
            logoutSession(session, now);
        }

        findActiveRefreshTokenBySession(session)
                .ifPresent(token -> {
                    token.revoke();
                    refreshTokenRepository.save(token, session);
                });
    }
    @Transactional public PasswordChangeRequest forgotPassword(Long employeeId, String ip, String deviceId){
        Instant now = Instant.now();
        User user = userRepository.findByEmployeeId(employeeId)
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
                    logoutSession(session, now);

                    findActiveRefreshTokenBySession(session)
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
    @Transactional public void changePassword(Long employeeId, String deviceId, Long passwordChangeRequestId, String newPassword) {
        Instant now = Instant.now();
        PasswordChangeRequest request =
                passwordChangeRequestRepository.findById(passwordChangeRequestId)
                        .orElseThrow(() ->
                                new IllegalStateException("Password change request not found")
                        );

        validatePasswordChangeRequest(request, deviceId, now);
        User user = userRepository.findByEmployeeId(employeeId)
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

    public User userRegister(@NotNull String username, Long employeeId, String password, String ip){
        //validar se a matricula do usuario está disponivel no jdbc
        return userRepository.save(
                User.create(
                        username,
                        passwordHasher.hash(password),
                        employeeId
                )
        );
    }
    public Session sessionRegister(@NotNull User user, String deviceId, String ip){
        return sessionRepository.save(
                Session.create(
                        user.getId(),
                        ip,
                        deviceId,
                        authTokenProperties.sessionTtl()
                )
        );
    }
    public IssuedRefreshToken refreshTokenRegister(@NotNull Session session) {
        String raw= tokenGenerator.generate();
        String hash = tokenHashService.hash(raw);

        RefreshToken token = RefreshToken.create(session, hash,authTokenProperties.refreshTokenTtl());
        RefreshToken saved = refreshTokenRepository.save(token, session);

        return new IssuedRefreshToken(saved, raw);

    }
    public Optional<Session> findSessionByUserAndDeviceIdAndSessionStatus(@NotNull User user, String deviceId, SessionStatus sessionStatus){
        return sessionRepository.findByUserIdAndDeviceIdAndSessionStatus(user.getId(), deviceId, sessionStatus);
    }
    public Optional<RefreshToken> findRefreshTokenByToken(@NotNull String refreshToken){
        String tokenHash = tokenHashService.hash(refreshToken);
        return refreshTokenRepository.findByTokenHash(tokenHash);

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
    private long getAccessTokenRemainingSeconds(Instant now, Instant accessTokenExpiresAt) {
        return Math.max(
                Duration.between(now, accessTokenExpiresAt).getSeconds(),
                0
        );
    }
    private void updateSessionLastIpAddressIfChanged(@NotNull Session session, String ip, Instant now) {
        if (!Objects.equals(session.getLastIpAddress(), ip)) {
            session.updateIpAddress(ip, now);
            sessionRepository.save(session);
        }
    }
    private void logoutSession(@NotNull Session session, Instant now) {
        if (session.isLoggedOut()) return;
        session.logout(now);
        sessionRepository.save(session);
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

            logoutSession(session,now);
        }
    }
    private void validatePasswordChangeRequest(@NotNull PasswordChangeRequest request, String deviceId, Instant now) {
        if (!request.isRequestedFromDevice(deviceId)) {
            throw new IllegalStateException("This request is from another device");
        }
        if (!request.canChangePassword(now)) {
            throw new IllegalStateException("Password change not allowed");
        }
    }
    public Optional<Session> findSessionById(Long sessionId){
        return sessionRepository.findById(sessionId);
    }
    public Optional<RefreshToken> findActiveRefreshTokenBySession(@NotNull Session session){
        return refreshTokenRepository.findBySessionIdAndRefreshTokenStatus(session.getId(), RefreshTokenStatus.ACTIVE);
    }

    public boolean userCanBeCreated(Long employeeId){
        return employeeRepository.isAuthorizedForUserCreation(employeeId);
    }
    public String nameFromEmployee(Long employeeId){
        return employeeRepository.nameByEmployee(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not authorized"));
    }
}
