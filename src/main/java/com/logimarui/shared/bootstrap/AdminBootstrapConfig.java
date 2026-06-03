package com.logimarui.shared.bootstrap;

import java.time.LocalDate;

public interface AdminBootstrapConfig {

    boolean enabled();

    String name();

    LocalDate birthDate();

    String email();

    String cpf();

    String password();

    String phoneNumber();

    String roleName();
}