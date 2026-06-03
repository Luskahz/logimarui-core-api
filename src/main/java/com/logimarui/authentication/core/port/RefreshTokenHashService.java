package com.logimarui.authentication.core.port;


public interface RefreshTokenHashService {
    String hash(String rawToken);
}
