package com.logimarui.auth.core.service;

import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import com.logimarui.auth.core.domain.enums.Role;
import com.logimarui.auth.core.domain.enums.SessionStatus;
import com.logimarui.auth.core.domain.exception.UserNotFoundException;
import com.logimarui.auth.core.domain.model.PasswordChangeRequest;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.repository.PasswordChangeRequestRepository;
import com.logimarui.auth.core.repository.RefreshTokenRepository;
import com.logimarui.auth.core.repository.SessionRepository;
import com.logimarui.auth.core.repository.UserRepository;
import com.logimarui.auth.infra.config.security.AuthTokenProperties;
import com.logimarui.auth.infra.security.jwt.JwtService;
import com.logimarui.auth.infra.security.principal.UserPrincipal;
import com.logimarui.auth.infra.security.token.TokenGenerator;
import com.logimarui.auth.infra.security.token.TokenHashService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;
    private final TokenHashService tokenHashService;
    private final JwtService jwtService;
    private final AuthTokenProperties authTokenProperties;

    @Transactional public AuthTokens login(@NotNull Long employeeId, String password, String ip, String deviceId) {
        Instant now = Instant.now();
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() ->
                    new UserNotFoundException("User not found for provided employee number")
                );
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            user.registerFailedLogin();
            userRepository.save(user);
            throw new SecurityException("wrong password");
        }
        user.recordSuccessfulLogin(now);
        userRepository.save(user);
        Session session = findSessionByUserAndDeviceId(user, deviceId)
                .map(existingSession -> {
                    if(existingSession.isValid(now)){
                        existingSession.updateIpAddress(ip, now);
                        return existingSession;
                    }
                    return sessionRegister(
                            user, deviceId, ip
                    );
                })
                .orElseGet(() -> sessionRegister(user, deviceId, ip));


        IssuedRefreshToken issuedRefreshToken = refreshTokenRegister(session);

        return new AuthTokens(
                issuedRefreshToken.rawToken(),
                jwtService.generateAccessToken(user, session),
                jwtService.getAccessTokenExpiresInSeconds()
                );
    }
    @Transactional public AuthTokens register(String username, Long employeeId, String password, String ip, String deviceId) {
        User user = userRegister(username, employeeId, password, ip);
        Session session = sessionRegister(user, deviceId, ip);
        IssuedRefreshToken issued = refreshTokenRegister(session);
        String rawRefreshToken = issued.rawToken();
        String accessToken = jwtService.generateAccessToken(user, session);

        return new AuthTokens(
                rawRefreshToken,
                accessToken,
                jwtService.getAccessTokenExpiresInSeconds()
        );
    }
    @Transactional public AuthContext me(@NotNull Authentication authentication, String ip, String deviceId) {
        Instant now = Instant.now();
        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new SecurityException("Invalid authentication principal");
        }

        List<String> roles = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        User user = getUserForAuthentication(principal.getUserId());

        Session session = findSessionById(principal.getSessionId())
                .orElseThrow(()-> new SecurityException("Session not found"));
        if (!Objects.equals(session.getUserId(), principal.getUserId())
                || !Objects.equals(session.getDeviceId(), deviceId)) {
            throw new SecurityException("Session does not belong to this user/device");
        }

        updateSessionLastIpAddressIfChanged(session, ip, now);

        return new AuthContext (
                principal.getUserId(),
                roles,
                principal.getSessionId(),
                session.isValid(now),
                getAccessTokenRemainingSeconds(principal)
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
        IssuedRefreshToken issued = rotateRefreshTokenFromExisting(existingToken, now);

        return new AuthTokens(
                issued.rawToken(),
                jwtService.generateAccessToken(user, issued.refreshToken().getSession()),
                jwtService.getAccessTokenExpiresInSeconds()
        );
    }
    @Transactional public void logout(Long userId, String ip, String deviceId) {
        Instant now = Instant.now();

        Optional<Session> sessionOpt =
                sessionRepository.findByUserIdAndDeviceId(userId, deviceId);

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
        if (!user.isActive()) {
            throw new IllegalStateException("User cannot request password change");
        }
        PasswordChangeRequest requestPending;

        Optional<PasswordChangeRequest> existing =
                passwordChangeRequestRepository.findActiveByUserId(user.getId());

        if (existing.isPresent()) {
            return existing.get();
        }

        sessionRepository
                .findByUserIdAndDeviceId(user.getId(), deviceId)
                .ifPresent(session -> {
                    session.logout(now);
                    sessionRepository.save(session);

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
                        authTokenProperties.getPasswordChangeRequestTtl()
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
                passwordEncoder.encode(newPassword),
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
                        passwordEncoder.encode(password),
                        employeeId
                )
        );
    }
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    public User getUserForAuthentication(Long userId) {
        User user = getUserById(userId);

        if (user.isBlockedForLogin()) {
            throw new IllegalStateException("Blocked user");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("User cannot authenticate");
        }

        return user;
    }
    public Session sessionRegister(@NotNull User user, String deviceId, String ip){

        return sessionRepository.save(
                Session.create(
                        user.getId(),
                        ip,
                        deviceId,
                        authTokenProperties.getSessionTtl()
                )
        );
    }
    public IssuedRefreshToken refreshTokenRegister(@NotNull Session session) {
        String raw= tokenGenerator.generate();
        String hash = tokenHashService.hash(raw);

        RefreshToken token = RefreshToken.create(session, hash,authTokenProperties.getRefreshTokenTtl());
        RefreshToken saved = refreshTokenRepository.save(token, session);

        return new IssuedRefreshToken(saved, raw);

    }
    public Optional<Session> findSessionByUserAndDeviceId(@NotNull User user, String deviceId){
        return sessionRepository.findByUserIdAndDeviceId(user.getId(), deviceId);
    }
    public Optional<RefreshToken> findRefreshTokenByToken(@NotNull String refreshToken){
        String tokenHash = tokenHashService.hash(refreshToken);
        return refreshTokenRepository.findByTokenHash(tokenHash);

    }
    private @NotNull IssuedRefreshToken rotateRefreshTokenFromExisting(@NotNull RefreshToken refreshToken, Instant now){
        String raw = tokenGenerator.generate();
        String hash = tokenHashService.hash(raw);
        refreshToken.rotate(hash, authTokenProperties.getRefreshTokenTtl(), now);
        refreshTokenRepository.save(refreshToken, refreshToken.getSession());

        return new IssuedRefreshToken(
                refreshToken,
                raw
        );
    }
    private long getAccessTokenRemainingSeconds(@NotNull UserPrincipal principal) {
        return Math.max(
                Duration.between(Instant.now(), principal.getAccessTokenExpiresAt()).getSeconds(),
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

            session.logout(now);
            sessionRepository.save(session);
        }
    }
    private void validatePasswordChangeRequest(
            PasswordChangeRequest request,
            String deviceId,
            Instant now
    ) {
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


}
