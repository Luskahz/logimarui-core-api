package com.logimarui.auth.core.service;

import com.logimarui.auth.api.dto.AuthTokenResponseDTO;
import com.logimarui.auth.api.dto.login.LoginRequestDTO;
import com.logimarui.auth.api.dto.register.RegisterRequestDTO;
import com.logimarui.auth.core.domain.model.Session;
import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.repository.RefreshTokenRepository;
import com.logimarui.auth.core.repository.SessionRepository;
import com.logimarui.auth.core.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RefreshTokenRepository refreshToken;
    private final PasswordEncoder passwordEncoder;

    public AuthTokenResponseDTO login(LoginRequestDTO request, String ip, String device) {
        throw new UnsupportedOperationException("TODO");
    }

    public AuthTokenResponseDTO register(RegisterRequestDTO request, String ip, String device) {
        User user = User.create(request.username(), passwordEncoder.encode( request.password()), request.matriculaRH());
        userRepository.save(user);
        Session session = Session.create(user.getId(), ip, device);
        sessionRepository.save(session);


    }

    public AuthTokenResponseDTO refresh(String refreshToken) {
        throw new UnsupportedOperationException("TODO");
    }

    public void logout(String accessToken) {
        throw new UnsupportedOperationException("TODO");
    }

    public void me(String accessToken) {
        throw new UnsupportedOperationException("TODO");
    }
}
