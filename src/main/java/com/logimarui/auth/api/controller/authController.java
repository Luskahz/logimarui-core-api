package com.logimarui.auth.api.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/")
public class authController {
    private final AuthService authService;
    private final JwtService jwtService;
}
