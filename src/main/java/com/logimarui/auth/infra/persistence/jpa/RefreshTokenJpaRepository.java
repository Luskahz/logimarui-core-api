package com.logimarui.auth.infra.persistence.jpa;


import com.logimarui.auth.core.domain.enums.RefreshTokenStatus;
import com.logimarui.auth.core.domain.model.RefreshToken;
import com.logimarui.auth.infra.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface RefreshTokenJpaRepository
        extends JpaRepository<RefreshTokenEntity, Long> {


    Optional<RefreshTokenEntity> findBySessionIdAndRefreshTokenStatus(
            Long sessionId,
            RefreshTokenStatus status
    );

    Optional<RefreshTokenEntity> findByTokenHash(
            String tokenHash
    );

    Optional<RefreshTokenEntity> findBySessionId(Long sessionId);
//mudar, o ip não deveria estar no refreshToken
    RefreshTokenEntity updateLastIpAddress(Long token, String ipAddress);

}
