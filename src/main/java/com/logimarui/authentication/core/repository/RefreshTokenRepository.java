package com.logimarui.authentication.core.repository;

import com.logimarui.authentication.core.domain.model.RefreshToken;
import com.logimarui.authentication.core.domain.model.Session;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken token, Session session);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

}