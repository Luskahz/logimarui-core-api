package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.changePassword.ChangePasswordRequestDTO;
import com.logimarui.auth.api.dto.forgotPassword.ForgotPasswordRequestDTO;
import com.logimarui.auth.api.dto.forgotPassword.ForgotPasswordResponseDTO;
import com.logimarui.auth.core.domain.model.PasswordChangeRequest;
import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.infra.web.RequestContextUtils;
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
    AuthService authService;

    @PostMapping("/forgot-password")
    public ForgotPasswordResponseDTO forgotPassword(
            HttpServletRequest httpServletRequest,
            @RequestBody @NotNull ForgotPasswordRequestDTO forgotPasswordRequestDTO
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        PasswordChangeRequest passwordChangeRequest = authService.forgotPassword(forgotPasswordRequestDTO.employeeId(), ip, deviceId);
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

        authService.changePassword(
                changePasswordRequestDTO.employeeId(),
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
        authService.authorizeChangePassword(principal.getUserId(), passwordChangeRequestId);
    }

}
