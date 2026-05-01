package com.logimarui.authentication.api.controller;

import com.logimarui.authentication.core.application.services.LogoutService;
import com.logimarui.authentication.infra.web.RequestContextUtils;
import com.logimarui.infra.security.principal.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Validated
public class LogoutController {
    LogoutService logoutService;


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


        logoutService.logout(
                principal.getUserId(),
                ip,
                deviceId
        );
    }
}
