package com.logimarui.authentication.infra.persistence.entity;

import com.logimarui.authentication.core.domain.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "auth_session",
        indexes = {
                @Index(
                        name = "idx_auth_session_user_device",
                        columnList = "user_id, device_id"
                ),
                @Index(
                        name = "idx_auth_session_user_device_status",
                        columnList = "user_id, device_id, session_status"
                )
        }
        //necessario criar indice de unicidade de sessão ativa para cada userId + deviceId
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", nullable = false, length = 50)
    private String deviceId;

    @Column(name = "last_ip_address", length = 45)
    private String lastIpAddress;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "logged_out_at")
    private Instant loggedOutAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "session_status", nullable = false, length = 30)
    private SessionStatus sessionStatus;
}

