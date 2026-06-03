package com.logimarui.authentication.core.application.services;

import com.logimarui.authentication.core.application.exceptions.user.UserCpfInvalidException;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.port.PasswordHasher;
import com.logimarui.authentication.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;

    public User registerUser(
            String name,
            LocalDate birthData,
            String email,
            String cpf,
            String rg,
            String passwordRaw,
            String phoneNumber

    ) {
        Instant now = Instant.now();

        if (userRepository.existsByCpf(cpf)) {
            throw new UserCpfInvalidException("The CPF number provided is already in use.");
        }

        String passwordHash = passwordHasher.hash(passwordRaw);

        User newUser = User.create(name, birthData, email, cpf, rg, passwordHash, phoneNumber, now);

        return userRepository.save(newUser);
    }
}