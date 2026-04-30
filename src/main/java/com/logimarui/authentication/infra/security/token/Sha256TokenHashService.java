package com.logimarui.authentication.infra.security.token;

import com.logimarui.authentication.core.port.TokenHashService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class Sha256TokenHashService implements TokenHashService {

    @Override
    public String hash(String rawToken) {
        return DigestUtils.sha256Hex(rawToken);
    }
}
