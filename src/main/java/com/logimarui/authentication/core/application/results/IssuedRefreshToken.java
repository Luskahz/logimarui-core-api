package com.logimarui.authentication.core.application.results;

import com.logimarui.authentication.core.domain.model.RefreshToken;

public record IssuedRefreshToken(
        RefreshToken refreshToken,
        String rawToken
) {}
