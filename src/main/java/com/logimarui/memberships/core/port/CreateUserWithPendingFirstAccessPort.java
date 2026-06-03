package com.logimarui.memberships.core.port;

public interface CreateUserWithPendingFirstAccessPort {

    Long createPendingUser(String name, String email, String cpf, String phoneNumber);
}
