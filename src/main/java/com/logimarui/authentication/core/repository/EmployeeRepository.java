package com.logimarui.authentication.core.repository;

import java.util.Optional;

public interface EmployeeRepository {

    boolean isAuthorizedForUserCreation(Long employeeId);
    Optional<String> nameByEmployee(Long employeeId);
}
