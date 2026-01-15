package com.logimarui.all.core.api.auth.controller;

import com.logimarui.all.core.api.auth.dto.LoginRequestDTO;
import com.logimarui.all.core.api.auth.dto.LoginResponseDTO;
import com.logimarui.all.core.api.auth.service.AuthService;
import com.logimarui.all.core.api.auth.service.JwtService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;

    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        return authService.login(request);
    }

}
