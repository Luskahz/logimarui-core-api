package com.logimarui.authorization.infra.persistence.jpa;

import com.logimarui.authorization.core.domain.enums.RoleStatus;
import com.logimarui.authorization.infra.persistence.entity.UserRoleEntity;
import com.logimarui.shared.authorization.PermissionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserPermissionQueryJpaRepository extends JpaRepository<UserRoleEntity, Long> {

    @Query("""
            select distinct permission.code
            from UserRoleEntity userRole
            join RolePermissionEntity rolePermission
                on rolePermission.role.id = userRole.role.id
            join PermissionEntity permission
                on permission.id = rolePermission.permission.id
            where userRole.userId = :userId
              and userRole.role.status = :activeStatus
            order by permission.code
            """)
    List<PermissionCode> findPermissionCodesByUserId(
            @Param("userId") Long userId,
            @Param("activeStatus") RoleStatus activeStatus
    );
}