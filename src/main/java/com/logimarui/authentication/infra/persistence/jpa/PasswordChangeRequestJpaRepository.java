package com.logimarui.authentication.infra.persistence.jpa;

import com.logimarui.authentication.core.domain.enums.PasswordChangeStatus;
import com.logimarui.authentication.infra.persistence.entity.PasswordChangeRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

public interface PasswordChangeRequestJpaRepository
        extends JpaRepository<PasswordChangeRequestEntity, Long> {

    @Query("""
        select p
        from PasswordChangeRequestEntity p
        where p.userId = :userId
          and p.passwordChangeStatus in (:activeStatuses)
          and p.expiresAt > :now
    """)
    Optional<PasswordChangeRequestEntity> findActiveByUserId(
            @Param("userId") Long userId,
            @Param("activeStatuses") Collection<PasswordChangeStatus> activeStatuses,
            @Param("now") Instant now
    );

    Optional<PasswordChangeRequestEntity> findById(Long id);
}
