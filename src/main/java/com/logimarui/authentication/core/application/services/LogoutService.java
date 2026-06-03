package com.logimarui.authentication.core.application.services;

import com.logimarui.authentication.core.application.exceptions.session.SessionNotFoundException;
import com.logimarui.authentication.core.application.exceptions.user.UserIdNotFoundException;
import com.logimarui.authentication.core.application.results.LogoutResult;
import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.core.repository.SessionRepository;
import com.logimarui.authentication.core.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public LogoutResult logout(Long userId) {
        Instant now = Instant.now();
        Session session = sessionRepository.findActiveByUserId(userId)
                .orElseThrow(() -> new SessionNotFoundException("Session not found."));

        userRepository.findById(session.getUserId())
                .orElseThrow(() -> new UserIdNotFoundException("User not found."));

        session.logout(now);
        sessionRepository.save(session);

        return new LogoutResult("Logout successful!");
    }
}