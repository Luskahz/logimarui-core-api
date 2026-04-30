package com.logimarui.authentication.core.port;

import com.logimarui.authentication.core.domain.model.IssuedAccessToken;
import com.logimarui.authentication.core.domain.model.Session;
import com.logimarui.authentication.core.domain.model.User;

public interface JwtService {
    IssuedAccessToken generateAccessToken(User user, Session session);
}