package com.logimarui.authentication.infra.adapter.vehicles;

import com.logimarui.authentication.infra.persistence.jpa.UserJpaRepository;
import com.logimarui.vehicles.core.port.UserPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPortImpl implements UserPort {

    private final UserJpaRepository userJpaRepository;

    @Override
    public boolean existsById(Long userId) {
        return userJpaRepository.existsById(userId);
    }
}
