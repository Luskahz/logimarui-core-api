package com.logimarui.authentication.api.controller;

import com.logimarui.authentication.api.dto.AuthTokenResponseDTO;
import com.logimarui.authentication.api.dto.refresh.RefreshRequestDTO;
import com.logimarui.authentication.core.application.results.AuthTokens;
import com.logimarui.authentication.core.application.services.RefreshService;
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
public class RefreshController {
    RefreshService refreshService;

    @PostMapping("/refresh")
    public AuthTokenResponseDTO refresh(
            @Valid @RequestBody @NotNull RefreshRequestDTO request,
            HttpServletRequest httpServletRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpServletRequest);
        String deviceId = RequestContextUtils.resolveDeviceId(httpServletRequest);


        AuthTokens tokens = refreshService.refresh(request.refreshToken(), ip, deviceId);

        return new AuthTokenResponseDTO(
                tokens.refreshToken(),
                tokens.accessToken(),
                tokens.expiresInSeconds()
        );
    }
}
