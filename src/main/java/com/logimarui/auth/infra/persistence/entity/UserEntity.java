package com.logimarui.auth.infra.persistence.entity;

import com.logimarui.auth.core.domain.enums.Role;
import com.logimarui.auth.core.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "auth_user")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long employeeId;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus userStatus;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant lastLoginAt;

    private Instant passwordChangedAt;

    @Column(nullable = false)
    private int failedLoginAttempts;


}

