package com.logimarui.authentication.core.repository;

import com.logimarui.authentication.core.domain.enums.PasswordRecoveryRequestMethod;
import com.logimarui.authentication.core.domain.model.PasswordRecoveryRequest;

import java.time.Instant;
import java.util.Optional;

public interface PasswordRecoveryRequestRepository {
    Optional<PasswordRecoveryRequest> findByTokenHash(String tokenHash);

    Optional<PasswordRecoveryRequest> findOpenByUserId(
            Long userId,
            Instant now
    );

    Optional<PasswordRecoveryRequest> findOpenByUserIdAndMethod(
            Long userId,
            PasswordRecoveryRequestMethod method,
            Instant now
    );

    PasswordRecoveryRequest save(PasswordRecoveryRequest request);
}
