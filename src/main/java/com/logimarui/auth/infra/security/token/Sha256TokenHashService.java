package com.logimarui.auth.infra.security.token;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class Sha256TokenHashService implements TokenHashService {

    @Override
    public String hash(String rawToken) {
        return DigestUtils.sha256Hex(rawToken);
    }
}
