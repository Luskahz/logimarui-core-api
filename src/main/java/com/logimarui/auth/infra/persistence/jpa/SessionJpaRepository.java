package com.logimarui.auth.infra.persistence.jpa;


import com.logimarui.auth.core.domain.enums.SessionStatus;
import com.logimarui.auth.infra.persistence.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface SessionJpaRepository extends JpaRepository<SessionEntity, Long>{
    List<SessionEntity> findByUserIdAndSessionStatus(Long userId, SessionStatus sessionStatus);
    Optional<SessionEntity> findByUserIdAndDeviceIdAndSessionStatus(Long userId, String deviceId, SessionStatus sessionStatus);

}
