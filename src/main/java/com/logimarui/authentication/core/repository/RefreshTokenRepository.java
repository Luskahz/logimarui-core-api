package com.logimarui.authentication.core.repository;

import com.logimarui.authentication.core.domain.enums.RefreshTokenStatus;
import com.logimarui.authentication.core.domain.model.RefreshToken;
import com.logimarui.authentication.core.domain.model.Session;
import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findValidByTokenHash(String tokenHash);

    RefreshToken save(RefreshToken token, Session session);

    void revokeBySessionId(Long sessionId);

    Optional<RefreshToken> findBySessionIdAndRefreshTokenStatus(Long sessionId, RefreshTokenStatus refreshTokenStatus);
    Optional<RefreshToken> findByTokenHash(String tokenHash);

}