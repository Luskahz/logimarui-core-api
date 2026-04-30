package com.logimarui.auth.core.port;

import com.logimarui.auth.core.domain.model.IssuedAccessToken;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;

public interface JwtService {
    IssuedAccessToken generateAccessToken(User user, Session session);
}