package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.Role;
import com.logimarui.auth.core.domain.enums.UserStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

     private Long id;
     private Long employeeId;
     private String username;
     private String passwordHash;
     private Set<Role> roles;
     private UserStatus userStatus;
     private Instant createdAt;
     private Instant lastLoginAt;
     private Instant passwordChangedAt;
     private int failedLoginAttempts;

    private User(String username, String passwordHash, Long employeeId) {
        this.employeeId = employeeId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roles = EnumSet.of(Role.INDEFINIDO);
        this.userStatus = UserStatus.ACTIVE;
        Instant now = Instant.now();
        this.createdAt = now;
        this.lastLoginAt = now;
        this.failedLoginAttempts = 0;
    }

    public Set<Role> getRoles() {
        return Set.copyOf(roles);
    }


    @Contract("_, _, _ -> new") public static @NotNull User create(String username, String passwordHash, Long employeeId) {
        return new User(username, passwordHash, employeeId);
    }


    public static @NotNull User reconstitute(
            Long id,
            Long employeeId,
            String username,
            String passwordHash,
            Set<Role> roles,
            UserStatus userStatus,
            Instant createdAt,
            Instant lastLoginAt,
            Instant passwordChangedAt,
            int failedLoginAttempts
    ) {
        User user = new User();
        user.id = id;
        user.employeeId = employeeId;
        user.username = username;
        user.passwordHash = passwordHash;
        if (roles == null || roles.isEmpty()) user.roles = EnumSet.of(Role.INDEFINIDO);
        else user.roles = EnumSet.copyOf(roles);
        user.userStatus = userStatus;
        user.createdAt = createdAt;
        user.lastLoginAt = lastLoginAt;
        user.passwordChangedAt = passwordChangedAt;
        user.failedLoginAttempts = failedLoginAttempts;
        return user;
    }


    public void addRole(Role role) {
        if (role == null) throw new IllegalArgumentException("role must not be null");
        if (isLocked()) throw new IllegalStateException("user is locked, roles cannot be added");

        roles.add(role);
        roles.remove(Role.INDEFINIDO);
    }

    public void removeRole(Role role) {
        if (role == null) throw new IllegalArgumentException("role must not be null");
        if (isLocked()) throw new IllegalStateException("user is locked, roles cannot be removed");

        boolean removed = roles.remove(role);
        if (!removed) throw new IllegalStateException("user does not have this role");

        if (roles.isEmpty()) roles.add(Role.INDEFINIDO);
    }
    public void registerFailedLogin() {
        if(isBlockedForLogin()){
            throw new IllegalStateException("User is not Available to login");
        }
        failedLoginAttempts++;

        if (failedLoginAttempts >= 5) {
            userStatus = UserStatus.LOCKED;
        }
    }
    public void changePassword(String newHash, Instant now) {
        this.passwordHash = newHash;
        this.passwordChangedAt = now;
        this.failedLoginAttempts = 0;
    }

    public boolean isBlockedForLogin() {
        return isLocked() || isInactive();
    }
    public boolean isActive() {
        return userStatus == UserStatus.ACTIVE;
    }
    public boolean isLocked() {
        return userStatus == UserStatus.LOCKED;
    }
    public boolean isInactive() {
        return userStatus == UserStatus.INACTIVE;
    }
    public void unlock() {
        if (userStatus != UserStatus.LOCKED) throw new IllegalStateException("User is not locked");
        this.userStatus = UserStatus.ACTIVE;
        this.failedLoginAttempts = 0;
    }
    public void reactivate() {
        if (userStatus != UserStatus.INACTIVE) throw new IllegalStateException("User is not inactive");
        this.userStatus = UserStatus.ACTIVE;
    }
    public void recordSuccessfulLogin(Instant now) {
        this.lastLoginAt = now;
        this.failedLoginAttempts = 0;
    }



}
