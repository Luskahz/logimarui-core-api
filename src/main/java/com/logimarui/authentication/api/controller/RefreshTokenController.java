package com.logimarui.authentication.api.controller;

import com.logimarui.authentication.api.dto.token.AuthTokenResponseDTO;
import com.logimarui.authentication.api.dto.token.RefreshTokenRequestDTO;
import com.logimarui.authentication.core.application.results.AuthTokens;
import com.logimarui.authentication.core.application.services.RefreshService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Authentication - Refresh Tokens",
        description = "Rotação e renovação de tokens de autenticação."
)
public class RefreshTokenController {

    private final RefreshService refreshService;

    @PostMapping("/refresh")
    @Operation(
            summary = "Renovar tokens",
            description = "Gera um novo access token e um novo refresh token a partir de um refresh token válido."
    )
    public ResponseEntity<AuthTokenResponseDTO> refresh(
            @RequestBody @Valid RefreshTokenRequestDTO request
    ) {
        AuthTokens tokens = refreshService.refresh(
                request.refreshToken()
        );

        return ResponseEntity.ok(
                AuthTokenResponseDTO.from(tokens)
        );
    }
}