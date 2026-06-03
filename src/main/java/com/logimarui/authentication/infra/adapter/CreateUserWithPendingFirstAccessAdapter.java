package com.logimarui.authentication.infra.adapter;

import com.logimarui.authentication.core.domain.enums.UserStatus;
import com.logimarui.authentication.core.domain.model.User;
import com.logimarui.authentication.core.port.PasswordHasher;
import com.logimarui.authentication.core.repository.UserRepository;
import com.logimarui.memberships.core.port.CreateUserWithPendingFirstAccessPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateUserWithPendingFirstAccessAdapter implements CreateUserWithPendingFirstAccessPort {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    @Override
    public Long createPendingUser(String name, String email, String cpf, String phoneNumber) {
        Instant now = Instant.now(clock);

        // Senha temporária aleatória — o usuário nunca vai usá-la diretamente,
        // pois o acesso inicial é feito pelo link de ativação.
        String temporaryPassword = UUID.randomUUID().toString();
        String passwordHash = passwordHasher.hash(temporaryPassword);

        User newUser = User.reconstitute(
                null,
                name,
                null,
                email,
                cpf,
                passwordHash,
                phoneNumber,
                null,
                UserStatus.PENDING_FIRST_ACCESS,
                null,
                now,
                null,
                null,
                null,
                0
        );

        User saved = userRepository.save(newUser);
        return saved.getId();
    }
}