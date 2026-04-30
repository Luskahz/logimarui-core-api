package com.logimarui.infra.security.principal;

import com.logimarui.authentication.core.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;


@AllArgsConstructor
public class UserPrincipal implements UserDetails {
    @Getter @NotNull private final Long userId;
    @Getter @NotNull private final Long sessionId;
    private final List<String> roles;
    @Getter private final Instant accessTokenExpiresAt;

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList();
    }
    @Override public String getUsername() {
        return userId.toString();
    }
    @Override public @Nullable String getPassword() {
        return null;
    }
    @Override public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }
    @Override public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }
    @Override public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
    @Override public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
    public boolean hasRole(String role) {
        return roles.contains(role);
    }
    public boolean hasRole(Role role) {
        return roles.contains(role.name());
    }
    public boolean hasAnyRole(Role... rolesToCheck) {
        for (Role role : rolesToCheck) {
            if (roles.contains(role.name())) {
                return true;
            }
        }
        return false;
    }
    public boolean hasAnyRole(String... rolesToCheck) {
        for (String role : rolesToCheck) {
            if (roles.contains(role)) {
                return true;
            }
        }
        return false;
    }


}
