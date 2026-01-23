package com.logimarui.auth.infra.persistence.entity;

import com.logimarui.auth.core.domain.enums.PasswordChangeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "auth_password_change_request",
        indexes = {
                @Index(
                        name = "idx_password_change_user_status",
                        columnList = "user_id, status"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PasswordChangeRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private PasswordChangeStatus status;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt;

    @Column(name = "authorized_at")
    private Instant authorizedAt;

    @Column(name = "authorized_by")
    private Long authorizedBy;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
