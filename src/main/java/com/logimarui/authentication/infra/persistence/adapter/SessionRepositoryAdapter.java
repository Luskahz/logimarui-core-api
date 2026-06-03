package com.logimarui.authentication.infra.persistence.adapter;

import com.logimarui.authentication.core.domain.enums.SessionStatus;
import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.core.repository.SessionRepository;
import com.logimarui.authentication.infra.persistence.jpa.SessionJpaRepository;
import com.logimarui.authentication.infra.persistence.mapper.SessionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryAdapter implements SessionRepository {

    private final SessionJpaRepository jpaRepository;

    @Override
    public Session save(Session session) {
        return SessionMapper.toDomain(jpaRepository.save(SessionMapper.toEntity(session)));
    }

    @Override
    public Optional<Session> findById(Long sessionId) {
        return jpaRepository.findById(sessionId)
                .map(SessionMapper::toDomain);
    }

    @Override
    public Optional<Session> findActiveByUserId(Long userId) {
        // ATUALIZADO: Chamando o método novo que blinda a aplicação contra sessões duplicadas
        return jpaRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(userId, SessionStatus.ACTIVE)
                .map(SessionMapper::toDomain);
    }
}