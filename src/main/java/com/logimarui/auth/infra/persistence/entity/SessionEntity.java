package com.logimarui.auth.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "logimarui_auth_session")
public class SessionEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Setter
    @Column(nullable = false)
    private boolean active;

    @Column(length = 50)
    private String device;

    @Setter
    @Column(name = "last_ip_address", length = 45)
    private String lastIpAddress;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;


    public SessionEntity(
            Long userId,
            boolean active,
            String device,
            String lastIpAddress,
            Instant createdAt,
            Instant expiresAt
    ) {
        this.userId = userId;
        this.active = active;
        this.device = device;
        this.lastIpAddress = lastIpAddress;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

}
