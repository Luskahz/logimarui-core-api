package com.logimarui.auth.api.controller;

import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.infra.web.RequestContextUtils;
import com.logimarui.infra.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.jaas.SecurityContextLoginModule;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;

public class LogoutController {
    AuthService authService;


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
}
