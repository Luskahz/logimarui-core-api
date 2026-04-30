package com.logimarui.auth.api.dto.changePassword;

import org.hibernate.validator.constraints.br.CPF;

public record ChangePasswordRequestDTO(
        Long passwordChangeRequestId,
        @CPF String cpf,
        String newPassword
) {}
