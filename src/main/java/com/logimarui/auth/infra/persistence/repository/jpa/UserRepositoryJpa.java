package com.logimarui.auth.infra.persistence.repository.jpa;


import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.repository.UserRepository;

import com.logimarui.auth.infra.persistence.entity.UserEntity;
import com.logimarui.auth.infra.persistence.jpa.UserJpaRepository;
import com.logimarui.auth.infra.persistence.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class UserRepositoryJpa implements UserRepository {

    private final UserJpaRepository jpa;

    @Override
    public Optional<User> findByUsername(String username) {
        return jpa.findByUsername(username)
                .map(UserMapper::toDomain);
    }


    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity saved = jpa.save(entity);
        return UserMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findByEmployeeId(Long employeeId) {
        return jpa.findByEmployeeId(employeeId)
                .map(UserMapper::toDomain);
    }
}

