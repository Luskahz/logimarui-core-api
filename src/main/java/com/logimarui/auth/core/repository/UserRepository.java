package com.logimarui.auth.core.repository;

import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.infra.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository {

    Optional<User> findByUsername(String username);

    Optional<User> findById(Long id);

    void save(User user);
}
