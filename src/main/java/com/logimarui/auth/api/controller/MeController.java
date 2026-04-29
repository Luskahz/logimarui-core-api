package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.AuthMeResponseDTO;
import com.logimarui.auth.core.application.results.AuthContext;
import com.logimarui.auth.core.application.services.AuthService;
import com.logimarui.auth.infra.web.RequestContextUtils;
import com.logimarui.infra.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Validated
public class MeController {
    AuthService authService;


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
}
