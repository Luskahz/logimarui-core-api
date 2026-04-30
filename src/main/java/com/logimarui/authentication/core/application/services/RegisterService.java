package com.logimarui.auth.core.application.services;

import com.logimarui.auth.core.domain.model.User;
import com.logimarui.auth.core.port.*;
import com.logimarui.auth.core.repository.*;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public User registerUser(String username, String cpf, String password, String ip){
        return userRegister(username, cpf, password, ip);
    }

    public User userRegister(@NotNull String username, String cpf, String password, String ip){
        return userRepository.save(
                User.create(
                        username,
                        passwordHasher.hash(password),
                        cpf
                )
        );
    }
}
