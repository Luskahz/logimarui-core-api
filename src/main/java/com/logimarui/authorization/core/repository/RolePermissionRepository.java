package com.logimarui.authorization.core.repository;

import com.logimarui.authorization.core.domain.model.Permission;
import com.logimarui.authorization.core.domain.model.RolePermission;

import java.util.List;

public interface RolePermissionRepository {

    RolePermission save(RolePermission rolePermission);

    boolean existsByRoleIdAndPermissionId(Long roleId, Long permissionId);

    List<Permission> findPermissionsByRoleId(Long roleId);

    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
