package com.logimarui.auth.api.dto.forgotPassword;


public record ForgotPasswordResponseDTO(
        Long passwordChangeRequestId,
        String passwordChangeStatus
) {
}
