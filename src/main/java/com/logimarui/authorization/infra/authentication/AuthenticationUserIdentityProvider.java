package com.logimarui.authorization.infra.authentication;

import com.logimarui.authentication.core.repository.UserRepository;
import com.logimarui.authorization.core.port.UserIdentityProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationUserIdentityProvider implements UserIdentityProvider {

    private final UserRepository userRepository;

    @Override
    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}