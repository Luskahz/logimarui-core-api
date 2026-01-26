package com.logimarui.auth.core.port;

public interface TokenHashService {
    String hash(String rawToken);
}
