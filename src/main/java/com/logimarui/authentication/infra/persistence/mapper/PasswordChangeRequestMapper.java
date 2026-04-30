package com.logimarui.authentication.infra.persistence.mapper;


import com.logimarui.authentication.core.domain.model.PasswordChangeRequest;
import com.logimarui.authentication.infra.persistence.entity.PasswordChangeRequestEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordChangeRequestMapper {
    public static PasswordChangeRequest toDomain(
            PasswordChangeRequestEntity entity
    ){
        return PasswordChangeRequest.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getRequestedIp(),
                entity.getRequestedDeviceId(),
                entity.getPasswordChangeStatus(),
                entity.getRequestedAt(),
                entity.getAuthorizedAt(),
                entity.getAuthorizedBy(),
                entity.getExpiresAt()
        );
    }

    public static PasswordChangeRequestEntity toEntity(PasswordChangeRequest request){
        return new PasswordChangeRequestEntity(
                request.getId(),
                request.getUserId(),
                request.getRequestedIp(),
                request.getRequestedDeviceId(),
                request.getPasswordChangeStatus(),
                request.getRequestedAt(),
                request.getDecidedAt(),
                request.getDecidedBy(),
                request.getExpiresAt()
        );
    }
}
