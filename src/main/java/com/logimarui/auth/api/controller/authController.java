package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.AuthMeResponseDTO;
import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.refresh.RefreshRequestDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.service.AuthContext;
import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.core.service.AuthTokens;
import com.logimarui.auth.infra.web.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/register") public AuthTokenResponseDTO register(
            @RequestBody @Valid RegisterRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        AuthTokens tokens = authService.register(
                request,
                ip,
                deviceId
        );

        return new AuthTokenResponseDTO(
                tokens.refreshToken(),
                tokens.accessToken(),
                tokens.expiresIn()
        );
    }

    @PostMapping("/login") public AuthTokenResponseDTO login(
            @RequestBody @Valid LoginRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
    String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
    String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

    AuthTokens tokens = authService.login(request, ip, deviceId);
    return new AuthTokenResponseDTO(
            tokens.refreshToken(),
            tokens.accessToken(),
            tokens.expiresIn()
    );

    }

    @GetMapping("/me") public AuthMeResponseDTO me(
            @NotNull Authentication authentication,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        AuthContext result = authService.me(authentication, ip, deviceId);

        return new AuthMeResponseDTO(
                result.userId(),
                result.roles(),
                result.sessionId(),
                result.sessionActive(),
                result.expiresInSeconds()
        );
    }


    @PostMapping("/refresh")
    public void refresh(
            @Valid @RequestBody RefreshRequestDTO refreshRequestDTO
    ){
         authService.refresh()


    }

    @PostMapping("/logout")
    public void logout(){

    }

    @PostMapping("/change-password")
    public void changePassword(){

    }

    @PostMapping("/forgot-password")
    public void forgotPassword(){

    }


}
