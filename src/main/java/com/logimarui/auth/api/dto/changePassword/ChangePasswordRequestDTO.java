package com.logimarui.auth.api.dto.changePassword;

public record ChangePasswordRequestDTO(
        Long passwordChangeRequestId,
        Long employeeId,
        String newPassword
) {}
