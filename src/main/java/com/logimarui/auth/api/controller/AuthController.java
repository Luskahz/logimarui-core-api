package com.logimarui.auth.api.controller;

import com.logimarui.auth.api.dto.AuthMeResponseDTO;
import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.changePassword.ChangePasswordRequestDTO;
import com.logimarui.auth.api.dto.forgotPassword.ForgotPasswordRequestDTO;
import com.logimarui.auth.api.dto.forgotPassword.ForgotPasswordResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.refresh.RefreshRequestDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.domain.model.PasswordChangeRequest;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.service.AuthContext;
import com.logimarui.auth.core.service.AuthService;
import com.logimarui.auth.core.service.AuthTokens;
import com.logimarui.infra.security.principal.UserPrincipal;
import com.logimarui.auth.infra.web.RequestContextUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Validated
public class AuthController {
    private AuthService authService;











}
