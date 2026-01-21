package com.logimarui.auth.infra.persistence.jpa;


import com.logimarui.auth.infra.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface SessionJpaRepository extends JpaRepository<SessionEntity, Long>{
    Optional<SessionEntity> findByUserIdAndActiveTrue(Long userId);
    Optional<SessionEntity> findByUserIdAndDeviceId(Long userId, String deviceId);

}
