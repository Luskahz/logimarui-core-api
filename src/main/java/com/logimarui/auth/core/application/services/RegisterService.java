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
    private final EmployeeRepository employeeRepository;

    public User registerUser(String username, Long employeeId, String password, String ip){
        if(!userCanBeCreated(employeeId)) throw new IllegalStateException("the Employee is not available to an new user");
        return userRegister(username, employeeId, password, ip);
    }

    public boolean userCanBeCreated(Long employeeId){
        return employeeRepository.isAuthorizedForUserCreation(employeeId);
    }

    public User userRegister(@NotNull String username, Long employeeId, String password, String ip){
        //validar se a matricula do usuario está disponivel no jdbc
        return userRepository.save(
                User.create(
                        username,
                        passwordHasher.hash(password),
                        employeeId
                )
        );
    }
}
