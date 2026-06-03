package com.logimarui.authorization.core.port;

public interface UserIdentityProvider {
    boolean existsById(Long userId);
}
