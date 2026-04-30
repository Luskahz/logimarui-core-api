package com.logimarui.auth.core.application.results;

import com.logimarui.auth.core.domain.model.RefreshToken;

public record IssuedRefreshToken(
        RefreshToken refreshToken,
        String rawToken
) {}
