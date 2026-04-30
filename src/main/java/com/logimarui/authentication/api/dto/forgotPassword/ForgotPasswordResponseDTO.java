package com.logimarui.authentication.api.dto.forgotPassword;


public record ForgotPasswordResponseDTO(
        Long passwordChangeRequestId,
        String passwordChangeStatus
) {
}
