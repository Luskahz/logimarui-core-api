package com.logimarui.auth.infra.security.token;

public interface TokenHashService {
    String hash(String rawToken);
}
