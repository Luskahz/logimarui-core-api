package com.logimarui.authorization.infra.persistence.jpa;

import com.logimarui.authorization.infra.persistence.entity.PermissionEntity;
import com.logimarui.shared.authorization.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionJpaRepository extends JpaRepository<PermissionEntity, Long> {

    Optional<PermissionEntity> findByCode(PermissionCode code);
}