package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.AuthMeResponseDTO;
import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.core.service.AuthTokens;
import com.logimarui.auth.infra.security.jwt.JwtService;
import com.logimarui.auth.infra.security.principal.UserPrincipal;
import com.logimarui.auth.infra.web.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

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
    public AuthTokenResponseDTO login(
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

    @GetMapping("/me")
    public AuthMeResponseDTO me(
            @NotNull Authentication authentication,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        List<String> roles = principal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        User user = authService.getUserForAuthentication(principal.getUserId());
        Session session = authService.findSessionByUserAndDeviceId(user,deviceId)
                .orElseThrow(()-> new SecurityException("Session not found"));
        long expiresInSeconds = Math.max(
                Duration.between(Instant.now(), principal.getAccessTokenExpiresAt()).getSeconds(),
                0
        );

        return new AuthMeResponseDTO(
                principal.getUserId(),
                roles,
                principal.getSessionId(),
                !session.isInvalid(Instant.now()),
                expiresInSeconds
        );
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


}
