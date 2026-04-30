package com.logimarui.authentication.infra.persistence.entity;

import com.logimarui.authentication.core.domain.enums.RefreshTokenStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
@Entity
@Table(
        name = "auth_refresh_token",
        indexes = {
                @Index(name = "idx_refresh_token_session", columnList = "session_id")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private Long sessionId;

    @Column(name = "token_hash", nullable = false, unique = true, length = 255)
    private String tokenHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "refresh_token_status", nullable = false, length = 30)
    private RefreshTokenStatus refreshTokenStatus;
}

