package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.core.service.AuthTokens;
import com.logimarui.auth.infra.security.jwt.JwtService;
import com.logimarui.auth.infra.web.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;
    private JwtService jwtService;

    @PostMapping("/register")
    public AuthTokenResponseDTO register(
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

    @PostMapping("/login")
    public void login(
            @RequestBody @Valid LoginRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
    String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
    String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

    }

    @PostMapping("/refresh")
    public void refresh(){

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

    @GetMapping("/me")
    public void me(){

    }
}
