package com.logimarui.auth.infra.persistence.jpa;


import com.logimarui.auth.infra.persistence.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface RefreshTokenJpaRepository
        extends JpaRepository<RefreshTokenEntity, Long> {

    @Modifying
    @Query("""
        update SessionEntity s
           set s.active = false
         where s.sessionId = :sessionId
           and s.active = true
    """)
    int revokeBySessionId(@Param("sessionId") Long sessionId);
}
