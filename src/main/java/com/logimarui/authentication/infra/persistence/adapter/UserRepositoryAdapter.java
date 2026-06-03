package com.logimarui.authentication.infra.persistence.adapter;

import com.logimarui.authentication.core.domain.enums.UserStatus;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.repository.UserRepository;
import com.logimarui.authentication.infra.persistence.entity.UserEntity;
import com.logimarui.authentication.infra.persistence.jpa.UserJpaRepository;
import com.logimarui.authentication.infra.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador Secundário / Adaptador de Repositório (Hexagonal Architecture).
 * Esta classe IMPLEMENTA a interface contida no CORE, mas injeta o componente do Spring.
 * Assim o Core não sabe quem faz as buscas ao banco, mascarando o Spring Data.
 */
@RequiredArgsConstructor
@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    @Override
    public User save(User user) {
        UserEntity entity = UserMapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entity);

        return UserMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByCpf(String cpf) {
        return jpaRepository.findByCpf(cpf)
                .map(UserMapper::toDomain);
    }

    @Override
    public boolean existsByCpf(String cpf) {
        return jpaRepository.existsByCpf(cpf);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public List<Long> findActiveUserIds() {
        return jpaRepository.findActiveUserIds();
    }

    @Override
    public boolean existsActiveById(Long id) {
        return jpaRepository.existsByIdAndStatus(
                id,
                UserStatus.ACTIVE
        );
    }
}