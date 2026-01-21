package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.model.User;

import java.util.Optional;


public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    User save(User user);
    Optional<User> findByMatricula(Long matricula);
}
