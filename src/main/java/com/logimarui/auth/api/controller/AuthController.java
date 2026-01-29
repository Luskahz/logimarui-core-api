package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.AuthMeResponseDTO;
import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.changePassword.ChangePasswordRequestDTO;
import com.logimarui.auth.api.dto.forgotPassword.ForgotPasswordRequestDTO;
import com.logimarui.auth.api.dto.forgotPassword.ForgotPasswordResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.refresh.RefreshRequestDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.domain.model.PasswordChangeRequest;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.service.AuthContext;
import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.core.service.AuthTokens;
import com.logimarui.infra.security.principal.UserPrincipal;
import com.logimarui.auth.infra.web.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Validated
public class AuthController {
    private AuthService authService;


    @GetMapping("/employees/{employeeId}/name")
    public String employeeName(
            @PathVariable Long employeeId
    ){
        return authService.nameFromEmployee(employeeId);
    }

    @PostMapping("/register")
    public AuthTokenResponseDTO registerAndLogin(
            @RequestBody @Valid @NotNull RegisterRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);
        User user = authService.registerUser(request.username(), request.employeeId(), request.password(), ip);
        AuthTokens tokens = authService.login(
                user.getEmployeeId(),
                request.password(),
                ip,
                deviceId
        );

        return new AuthTokenResponseDTO(
                tokens.refreshToken(),
                tokens.accessToken(),
                tokens.expiresInSeconds()
        );
    }

    @PostMapping("/login") public AuthTokenResponseDTO login(
            @RequestBody @Valid @NotNull LoginRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
    String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
    String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

    AuthTokens tokens = authService.login(request.employeeId(), request.password(), ip, deviceId);
    return new AuthTokenResponseDTO(
            tokens.refreshToken(),
            tokens.accessToken(),
            tokens.expiresInSeconds()
    );

    }

    @GetMapping("/me") public AuthMeResponseDTO me(
            @NotNull Authentication authentication,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();


        AuthContext result = authService.me(
                principal.getUserId(),
                principal.getSessionId(),
                principal.getAccessTokenExpiresAt(),
                ip,
                deviceId
        );

        return new AuthMeResponseDTO(
                result.userId(),
                roles,
                result.sessionId(),
                result.sessionActive(),
                result.expiresInSeconds()
        );
    }

    @PostMapping("/refresh")
    public AuthTokenResponseDTO refresh(
            @Valid @RequestBody @NotNull RefreshRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        AuthTokens tokens = authService.refresh(request.refreshToken(), ip, deviceId);

        return new AuthTokenResponseDTO(
                tokens.refreshToken(),
                tokens.accessToken(),
                tokens.expiresInSeconds()
        );
    }

    @PostMapping("/logout")
    public void logout(
            @NotNull Authentication authentication,
            HttpServletRequest httpServletRequest
    ){
        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new SecurityException("Invalid authentication principal");
        }
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        authService.logout(
                principal.getUserId(),
                ip,
                deviceId
        );
    }

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
            @NotNull @PathVariable Long passwordChangeRequestId
    ){
        if (!(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new SecurityException("Invalid authentication principal");
        }
        authService.authorizeChangePassword(principal.getUserId(), passwordChangeRequestId);
    }
}
