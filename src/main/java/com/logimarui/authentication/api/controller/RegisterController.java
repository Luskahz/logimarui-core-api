package com.logimarui.authentication.api.controller;


import com.logimarui.authentication.api.dto.AuthTokenResponseDTO;
import com.logimarui.authentication.api.dto.register.RegisterRequestDTO;
import com.logimarui.authentication.core.application.services.LoginService;
import com.logimarui.authentication.core.application.services.RegisterService;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.application.results.AuthTokens;
import com.logimarui.authentication.infra.web.RequestContextUtils;
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
    RegisterService registerService;
    LoginService loginService;

    @PostMapping("/register")
    public AuthTokenResponseDTO registerAndLogin(
            @RequestBody @Valid @NotNull RegisterRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);
        User user = registerService.registerUser(request.username(), request.cpf(), request.password(), ip);
        AuthTokens tokens = loginService.login(
                user.getCpf(),
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
