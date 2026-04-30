package com.logimarui.authentication.core.port;

public interface TokenHashService {
    String hash(String rawToken);
}
