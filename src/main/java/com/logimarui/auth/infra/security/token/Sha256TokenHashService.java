package com.logimarui.auth.infra.security.token;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

@Component
public class Sha256TokenHashService implements TokenHashService{

    @Override
    public String hash(String rawToken) {
        return DigestUtils.sha256Hex(rawToken);
    }
}
