package com.logimarui.authorization.infra.persistence.adapter;

import com.logimarui.authorization.core.domain.model.Permission;
import com.logimarui.authorization.core.repository.PermissionRepository;
import com.logimarui.authorization.infra.persistence.entity.PermissionEntity;
import com.logimarui.authorization.infra.persistence.jpa.PermissionJpaRepository;
import com.logimarui.authorization.infra.persistence.mapper.PermissionMapper;
import com.logimarui.shared.authorization.PermissionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PermissionRepositoryAdapter implements PermissionRepository {

    private final PermissionJpaRepository jpa;
    private final PermissionMapper mapper;

    @Override
    public List<Permission> findAll() {
        return jpa.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return jpa.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Permission> findByCode(PermissionCode code) {
        return jpa.findByCode(code)
                .map(mapper::toDomain);
    }

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = mapper.toEntity(permission);

        PermissionEntity savedEntity = jpa.save(entity);

        return mapper.toDomain(savedEntity);
    }
}