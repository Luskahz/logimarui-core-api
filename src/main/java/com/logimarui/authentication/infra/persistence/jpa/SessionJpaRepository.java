package com.logimarui.authentication.infra.persistence.jpa;

import com.logimarui.authentication.core.domain.enums.SessionStatus;
import com.logimarui.authentication.infra.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionJpaRepository extends JpaRepository<SessionEntity, Long> {

    // ATUALIZADO: Agora busca apenas a PRIMEIRA sessão, ordenando da mais recente para a mais antiga
    Optional<SessionEntity> findFirstByUserIdAndStatusOrderByCreatedAtDesc(Long userId, SessionStatus status);
}