package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.core.application.results.AuthTokens;
import com.logimarui.auth.core.application.services.LoginService;
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
public class LoginController {

    LoginService loginService;


    @PostMapping("/login") public AuthTokenResponseDTO login(
            @RequestBody @Valid @NotNull LoginRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        AuthTokens tokens = loginService.login(request.employeeId(), request.password(), ip, deviceId);
        return new AuthTokenResponseDTO(
                tokens.refreshToken(),
                tokens.accessToken(),
                tokens.expiresInSeconds()
        );

    }

}
