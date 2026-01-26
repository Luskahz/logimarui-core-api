package com.logimarui.auth.infra.persistence.repository.jpa;

import com.logimarui.auth.core.domain.enums.PasswordChangeStatus;
import com.logimarui.auth.core.domain.model.PasswordChangeRequest;
import com.logimarui.auth.core.repository.PasswordChangeRequestRepository;
import com.logimarui.auth.infra.persistence.entity.PasswordChangeRequestEntity;
import com.logimarui.auth.infra.persistence.jpa.PasswordChangeRequestJpaRepository;
import com.logimarui.auth.infra.persistence.mapper.PasswordChangeRequestMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;



@Repository
@AllArgsConstructor
public class PasswordChangeRequestRepositoryJpa implements PasswordChangeRequestRepository {
    private final PasswordChangeRequestJpaRepository jpa;

    @Override
    public PasswordChangeRequest save(PasswordChangeRequest passwordChangeRequest) {
        PasswordChangeRequestEntity entity = PasswordChangeRequestMapper.toEntity(passwordChangeRequest);
        PasswordChangeRequestEntity saved = jpa.save(entity);
        return PasswordChangeRequestMapper.toDomain(saved);
    }

    @Override
    public Optional<PasswordChangeRequest> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<PasswordChangeRequest> findActiveByUserId(Long userId) {
        return jpa.findActiveByUserId(
                        userId,
                        List.of(
                                PasswordChangeStatus.REQUESTED,
                                PasswordChangeStatus.AUTHORIZED
                        ),
                        Instant.now()
                )
                .map(PasswordChangeRequestMapper::toDomain);
    }
}
