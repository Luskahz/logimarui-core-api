package com.logimarui.authentication.core.domain.model;

import com.logimarui.authentication.core.domain.enums.PasswordChangeStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private Instant decidedAt;
    private Long decidedBy;
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

    public static @NotNull PasswordChangeRequest create(
            Long userId,
            String requestedIp,
            String requestedDeviceId,
            Duration ttl
    ) {
        if (userId == null) throw new IllegalStateException("userId is required");
        if (requestedIp == null || requestedIp.isBlank()) throw new IllegalStateException("requestedIp is required");
        if (requestedDeviceId == null || requestedDeviceId.isBlank()) throw new IllegalStateException("requestedDeviceId is required");
        if (ttl == null || ttl.isZero() || ttl.isNegative()) throw new IllegalStateException("invalid ttl");

        Instant now = Instant.now();
        Instant expiresAt = now.plus(ttl);

        if (!expiresAt.isAfter(now)) {
            throw new IllegalStateException("expiresAt must be after requestedAt");
        }

        return new PasswordChangeRequest(
                userId,
                requestedIp,
                requestedDeviceId,
                PasswordChangeStatus.REQUESTED,
                now,
                expiresAt
        );
    }



    public static @NotNull PasswordChangeRequest reconstitute(
            Long id,
            Long userId,
            String requestedIp,
            String requestedDeviceId,
            PasswordChangeStatus passwordChangeStatus,
            Instant requestedAt,
            Instant decidedAt,
            Long decidedBy,
            @NotNull Instant expiresAt
    ) {
        if (userId == null) throw new IllegalStateException("userId is required");
        if (requestedIp == null || requestedIp.isBlank()) throw new IllegalStateException("requestedIp is required");
        if (requestedDeviceId == null || requestedDeviceId.isBlank()) throw new IllegalStateException("requestedDeviceId is required");
        if (passwordChangeStatus == null) throw new IllegalStateException("passwordChangeStatus is required");
        if (requestedAt == null) throw new IllegalStateException("requestedAt is required");
        if (expiresAt == null) throw new IllegalStateException("expiresAt is required");

        if (!expiresAt.isAfter(requestedAt)) {
            throw new IllegalStateException("expiresAt must be after requestedAt");
        }

        switch (passwordChangeStatus) {
            case REQUESTED -> {
                if (decidedAt != null || decidedBy != null)
                    throw new IllegalStateException("REQUESTED cannot have decision data");
            }
            case AUTHORIZED, REJECTED, COMPLETED -> {
                if (decidedAt == null || decidedBy == null)
                    throw new IllegalStateException("Decision required");
                if (decidedAt.isBefore(requestedAt))
                    throw new IllegalStateException("decidedAt cannot be before requestedAt");
            }
        }

        PasswordChangeRequest r = new PasswordChangeRequest();
        r.id = id;
        r.userId = userId;
        r.requestedIp = requestedIp;
        r.requestedDeviceId = requestedDeviceId;
        r.passwordChangeStatus = passwordChangeStatus;
        r.requestedAt = requestedAt;
        r.decidedAt = decidedAt;
        r.decidedBy = decidedBy;
        r.expiresAt = expiresAt;
        return r;
    }


    public void authorize(Long deciderId, Instant now) {
        if (!canBeResolved(now)) {
            throw new IllegalStateException("Password change request cannot be resolved");
        }
        this.passwordChangeStatus = PasswordChangeStatus.AUTHORIZED;
        this.decidedAt = now;
        this.decidedBy = deciderId;
    }

    public void reject(Long deciderId, Instant now) {
        if (!canBeResolved(now)) {
            throw new IllegalStateException("Password change request cannot be resolved");
        }
        this.passwordChangeStatus = PasswordChangeStatus.REJECTED;
        this.decidedAt = now;
        this.decidedBy = deciderId;
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
    public boolean canBeResolved(Instant now) {
        return passwordChangeStatus == PasswordChangeStatus.REQUESTED
                && !isExpired(now);
    }

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
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
