package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.model.PasswordChangeRequest;

import java.util.Optional;

public interface PasswordChangeRequestRepository {
    PasswordChangeRequest save(PasswordChangeRequest passwordChangeRequest);
    Optional<PasswordChangeRequest> findById(Long id);
    Optional<PasswordChangeRequest> findActiveByUserId(Long id);

}
