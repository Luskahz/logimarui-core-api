package com.logimarui.auth.infra.security.jwt;

import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;

public interface JwtService {
    public String generateAccessToken(User user, Session session);
    public long getAccessTokenExpiresInSeconds();
}
