package com.logimarui.authentication.core.application.services;

import com.logimarui.authentication.core.application.exceptions.session.SessionNotFoundException;
import com.logimarui.authentication.core.application.exceptions.session.SessionUserMismatchException;
import com.logimarui.authentication.core.application.exceptions.user.UserIdNotFoundException;
import com.logimarui.authentication.core.application.results.MeResult;
import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.repository.SessionRepository;
import com.logimarui.authentication.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final Clock clock;

    @Transactional(readOnly = true)
    public MeResult me(
            Long userId,
            Long sessionId,
            Instant accessTokenExpiresAt
    ) {
        Objects.requireNonNull(userId, "userId cannot be null");
        Objects.requireNonNull(sessionId, "sessionId cannot be null");
        Objects.requireNonNull(accessTokenExpiresAt, "accessTokenExpiresAt cannot be null");

        Instant now = Instant.now(clock);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIdNotFoundException("User not found"));

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found"));

        if (!session.getUserId().equals(user.getId())) {
            throw new SessionUserMismatchException("Session does not belong to this user");
        }

        return new MeResult(
                user.getId(),
                user.getName(),
                session.getId(),
                session.isValid(now),
                getAccessTokenRemainingSeconds(now, accessTokenExpiresAt)
        );
    }

    private long getAccessTokenRemainingSeconds(
            Instant now,
            Instant accessTokenExpiresAt
    ) {
        return Math.max(
                Duration.between(now, accessTokenExpiresAt).getSeconds(),
                0
        );
    }
}