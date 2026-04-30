package com.logimarui.authentication.infra.persistence.repository.jpa;


import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.repository.UserRepository;

import com.logimarui.authentication.infra.persistence.entity.UserEntity;
import com.logimarui.authentication.infra.persistence.jpa.UserJpaRepository;
import com.logimarui.authentication.infra.persistence.mapper.UserMapper;
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
    public Optional<User> findByCpf(String cpf) {
        return jpa.findByCpf(cpf)
                .map(UserMapper::toDomain);
    }
}

