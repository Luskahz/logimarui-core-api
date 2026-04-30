package com.logimarui.auth.infra.persistence.entity;

import com.logimarui.auth.core.domain.enums.Role;
import com.logimarui.auth.core.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(
        name = "auth_user",
        indexes = {
                @Index(name = "idxAuthUserCpf", columnList = "cpf"),
                @Index(name = "idxAuthUserUsername", columnList = "username")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cpf", nullable = false, unique = true)
    private String cpf;

    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "auth_user_role",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            )
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false, length = 30)
    private UserStatus userStatus;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;
}
