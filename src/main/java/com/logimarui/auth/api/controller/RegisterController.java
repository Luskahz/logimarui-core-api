package com.logimarui.auth.api.controller;


import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.core.service.AuthTokens;
import com.logimarui.auth.infra.web.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Validated
public class RegisterController {
    AuthService authService;

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

}
