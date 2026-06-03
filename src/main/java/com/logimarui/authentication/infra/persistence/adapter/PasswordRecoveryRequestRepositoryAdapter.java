package com.logimarui.authentication.infra.persistence.adapter;

import com.logimarui.authentication.core.domain.enums.PasswordRecoveryRequestMethod;
import com.logimarui.authentication.core.domain.enums.PasswordRecoveryRequestStatus;
import com.logimarui.authentication.core.domain.model.PasswordRecoveryRequest;
import com.logimarui.authentication.core.repository.PasswordRecoveryRequestRepository;
import com.logimarui.authentication.infra.persistence.jpa.PasswordRecoveryRequestJpaRepository;
import com.logimarui.authentication.infra.persistence.mapper.PasswordRecoveryRequestMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PasswordRecoveryRequestRepositoryAdapter implements PasswordRecoveryRequestRepository {

    private final PasswordRecoveryRequestJpaRepository jpaRepository;

    @Override
    public PasswordRecoveryRequest save(PasswordRecoveryRequest request) {
        return PasswordRecoveryRequestMapper.toDomain(
                jpaRepository.save(
                        PasswordRecoveryRequestMapper.toEntity(request)
                )
        );
    }

    @Override
    public Optional<PasswordRecoveryRequest> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash)
                .map(PasswordRecoveryRequestMapper::toDomain);
    }

    @Override
    public Optional<PasswordRecoveryRequest> findOpenByUserId(
            Long userId,
            Instant now
    ) {
        return jpaRepository.findFirstByUserIdAndStatusAndExpiresAtAfterOrderByCreatedAtDesc(
                        userId,
                        PasswordRecoveryRequestStatus.OPEN,
                        now
                )
                .map(PasswordRecoveryRequestMapper::toDomain);
    }

    @Override
    public Optional<PasswordRecoveryRequest> findOpenByUserIdAndMethod(
            Long userId,
            PasswordRecoveryRequestMethod method,
            Instant now
    ) {
        return jpaRepository.findFirstByUserIdAndStatusAndMethodAndExpiresAtAfterOrderByCreatedAtDesc(
                        userId,
                        PasswordRecoveryRequestStatus.OPEN,
                        method,
                        now
                )
                .map(PasswordRecoveryRequestMapper::toDomain);
    }
}