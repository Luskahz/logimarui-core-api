package com.logimarui.auth.core.domain.model;

import com.logimarui.auth.core.domain.enums.PasswordChangeStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;


@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordChangeRequest {

    private Long id;
    private Long userId;
    private String requestedIp;
    private String requestedDeviceId;
    private PasswordChangeStatus passwordChangeStatus;
    private Instant requestedAt;
    @Setter private Instant authorizedAt;
    @Setter private Long authorizedBy;
    private Instant expiresAt;


    private PasswordChangeRequest(
            Long userId,
            String requestedIp,
            String requestedDeviceId,
            PasswordChangeStatus passwordChangeStatus,
            Instant requestedAt,
            Instant expiresAt
    ) {
        this.userId = userId;
        this.requestedIp = requestedIp;
        this.requestedDeviceId = requestedDeviceId;
        this.passwordChangeStatus = passwordChangeStatus;
        this.requestedAt = requestedAt;
        this.expiresAt = expiresAt;
    }

    public static @NotNull PasswordChangeRequest create(Long userId, String requestedIp, String requestedDeviceId, Duration ttl) {
        Instant now = Instant.now();

        return new PasswordChangeRequest(
                userId,
                requestedIp,
                requestedDeviceId,
                PasswordChangeStatus.REQUESTED,
                now,
                now.plus(ttl)
        );
    }

    public static @NotNull PasswordChangeRequest reconstitute(
            Long id,
            Long userId,
            String requestedIp,
            String requestedDeviceId,
            PasswordChangeStatus passwordChangeStatus,
            Instant requestedAt,
            Instant authorizedAt,
            Long authorizedBy,
            Instant expiresAt
    ) {
        PasswordChangeRequest passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.id = id;
        passwordChangeRequest.userId = userId;
        passwordChangeRequest.requestedIp = requestedIp;
        passwordChangeRequest.requestedDeviceId = requestedDeviceId;
        passwordChangeRequest.passwordChangeStatus = passwordChangeStatus;
        passwordChangeRequest.requestedAt = requestedAt;
        passwordChangeRequest.authorizedAt = authorizedAt;
        passwordChangeRequest.authorizedBy = authorizedBy;
        passwordChangeRequest.expiresAt = expiresAt;
        return passwordChangeRequest;
    }

    public void authorize(Long authorizerId, Instant now) {
        if (isExpired(now)) {
            throw new IllegalStateException("Password change request expired");
        }
        if (passwordChangeStatus != PasswordChangeStatus.REQUESTED) {
            throw new IllegalStateException("Password change request not in REQUESTED state");
        }
        this.passwordChangeStatus = PasswordChangeStatus.AUTHORIZED;
        this.authorizedAt = now;
        this.authorizedBy = authorizerId;
    }

    public void reject(Long authorizerId, Instant now) {
        if (isExpired(now)) {
            throw new IllegalStateException("Password change request expired");
        }
        if (passwordChangeStatus != PasswordChangeStatus.REQUESTED) {
            throw new IllegalStateException("Password change request not in REQUESTED state");
        }
        this.passwordChangeStatus = PasswordChangeStatus.REJECTED;
        this.authorizedAt = now;
        this.authorizedBy = authorizerId;
    }

    public void complete(Instant now) {
        if (isExpired(now)) {
            throw new IllegalStateException("Password change request expired");
        }
        if (passwordChangeStatus != PasswordChangeStatus.AUTHORIZED) {
            throw new IllegalStateException("Password change request not AUTHORIZED");
        }
        this.passwordChangeStatus = PasswordChangeStatus.COMPLETED;
    }

    public boolean isExpired(Instant now) {
        return expiresAt.isBefore(now);
    }
    public boolean canChangePassword(Instant now) {
        return passwordChangeStatus == PasswordChangeStatus.AUTHORIZED
                && !isExpired(now);
    }
    public boolean isRequestedFromDevice(String deviceId) {
        return requestedDeviceId != null
                && requestedDeviceId.equals(deviceId);
    }
    public boolean isRequested() {
        return passwordChangeStatus == PasswordChangeStatus.REQUESTED;
    }

    public boolean isAuthorized() {
        return passwordChangeStatus == PasswordChangeStatus.AUTHORIZED;
    }

    public boolean isCompleted() {
        return passwordChangeStatus == PasswordChangeStatus.COMPLETED;
    }

    public boolean isRejected() {
        return passwordChangeStatus == PasswordChangeStatus.REJECTED;
    }


}
