package com.logimarui.authentication.core.repository;

import com.logimarui.authentication.core.domain.model.PasswordChangeRequest;

import java.util.Optional;

public interface PasswordChangeRequestRepository {
    PasswordChangeRequest save(PasswordChangeRequest passwordChangeRequest);
    Optional<PasswordChangeRequest> findById(Long id);
    Optional<PasswordChangeRequest> findActiveByUserId(Long id);

}
