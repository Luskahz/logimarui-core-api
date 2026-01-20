package com.logimarui.auth.infra.security.token;

import java.security.SecureRandom;
import java.util.Base64;

public class SEcureRandomTokenGenerator implements TokenGenerator{
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate() {
        byte[] bytes = new byte[64];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
