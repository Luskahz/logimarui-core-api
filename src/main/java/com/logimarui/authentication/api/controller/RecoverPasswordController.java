package com.logimarui.authentication.api.controller;

import com.logimarui.authentication.api.dto.changePassword.ChangePasswordRequestDTO;
import com.logimarui.authentication.api.dto.forgotPassword.ForgotPasswordRequestDTO;
import com.logimarui.authentication.api.dto.forgotPassword.ForgotPasswordResponseDTO;
import com.logimarui.authentication.core.application.services.RecoverPasswordService;
import com.logimarui.authentication.core.domain.model.PasswordChangeRequest;
import com.logimarui.authentication.infra.web.RequestContextUtils;
import com.logimarui.infra.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

public class RecoverPasswordController {
    RecoverPasswordService recoverPasswordService;

    @PostMapping("/forgot-password")
    public ForgotPasswordResponseDTO forgotPassword(
            HttpServletRequest httpServletRequest,
            @RequestBody @NotNull ForgotPasswordRequestDTO forgotPasswordRequestDTO
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        PasswordChangeRequest passwordChangeRequest = recoverPasswordService.forgotPassword(forgotPasswordRequestDTO.cpf(), ip, deviceId);
        return new ForgotPasswordResponseDTO(
                passwordChangeRequest.getId(),
                passwordChangeRequest.getPasswordChangeStatus().name()
        );
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @NotNull @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO,
            HttpServletRequest httpServletRequest
    ){
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        recoverPasswordService.changePassword(
                changePasswordRequestDTO.cpf(),
                deviceId,
                changePasswordRequestDTO.passwordChangeRequestId(),
                changePasswordRequestDTO.newPassword()
        );


    }

    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'ADMINISTRATIVO')")
    @PostMapping("/change-password/{id}/authorize")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void authorizeChangePassword(
            @NotNull Authentication authentication,
            @NotNull @PathVariable("id") Long passwordChangeRequestId
    ){
        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new SecurityException("Invalid authentication principal");
        }
        recoverPasswordService.authorizeChangePassword(principal.getUserId(), passwordChangeRequestId);
    }

}
