package com.logimarui.core.api.auth.service;

import com.logimarui.core.api.auth.dto.LoginRequestDTO;
import com.logimarui.core.api.auth.dto.LoginResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JwtService jwtService;

    public AuthService(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        if (request.usuario() == null || request.senha() == null) {
            throw new IllegalArgumentException("Usuário e senha obrigatórios");
        }

        String token = jwtService.gerarToken(request.usuario());

        return new LoginResponseDTO(token);
    }
}
