package com.logimarui.auth.infra.persistence.entity;

import com.logimarui.auth.core.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "logimarui_user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean locked;

    @Column(nullable = false)
    private int failedLoginAttempts;

    private Instant passwordChangedAt;
    private Instant lastLoginAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}

