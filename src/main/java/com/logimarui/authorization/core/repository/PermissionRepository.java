package com.logimarui.authorization.core.repository;

import com.logimarui.authorization.core.domain.model.Permission;
import com.logimarui.shared.authorization.PermissionCode;

import java.util.List;
import java.util.Optional;

public interface PermissionRepository {

    List<Permission> findAll();

    Optional<Permission> findById(Long id);

    Optional<Permission> findByCode(PermissionCode code);

    Permission save(Permission permission);
}
