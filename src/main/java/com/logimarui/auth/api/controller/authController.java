package com.logimarui.auth.api.controller;


import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.infra.web.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;

    @PostMapping("/register")
    public AuthTokenResponseDTO register(
            @RequestBody @Valid RegisterRequestDTO request,
            HttpServletRequest httpRequest
    ){
        String ip = RequestContextUtils.resolveClientIp(httpRequest);
        String device = RequestContextUtils.resolveDevice(httpRequest);
        return authService.register(request, ip, device);
    }

    @PostMapping("/login")
    public void login(){


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

    @GetMapping("/me")
    public void me(){

    }
}
