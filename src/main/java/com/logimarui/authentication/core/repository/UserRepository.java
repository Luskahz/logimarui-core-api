package com.logimarui.authentication.core.repository;

import com.logimarui.authentication.core.domain.model.User;

import java.util.Optional;


public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    User save(User user);
    Optional<User> findByCpf(String cpf);

}
