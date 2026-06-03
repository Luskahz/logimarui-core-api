package com.logimarui.authentication.api.controller;

import com.logimarui.authentication.api.dto.token.AuthTokenResponseDTO;
import com.logimarui.authentication.api.dto.user.UserRegistrationRequestDTO;
import com.logimarui.authentication.core.application.results.AuthTokens;
import com.logimarui.authentication.core.application.services.LoginService;
import com.logimarui.authentication.core.application.services.RegisterService;
import com.logimarui.authentication.core.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        name = "Authentication - User Registration",
        description = "Registro público de usuários e emissão inicial de tokens."
)
public class UserController {

    private final RegisterService registerService;
    private final LoginService loginService;

    @PostMapping("/register")
    @Operation(
            summary = "Registrar usuário",
            description = "Cria uma nova conta de usuário e retorna os tokens de autenticação."
    )
    public ResponseEntity<AuthTokenResponseDTO> register(
            @RequestBody @Valid UserRegistrationRequestDTO request
    ) {
        User user = registerService.registerUser(
                request.name(),
                request.birthData(),
                request.email(),
                request.cpf(),
                request.rg(),
                request.password(),
                request.phoneNumber()
        );

        AuthTokens tokens = loginService.authenticateRegisteredUser(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AuthTokenResponseDTO.from(tokens));
    }
}