package com.logimarui.auth.infra.persistence.repository.jpa;

import com.logimarui.auth.core.repository.EmployeeRepository;
import com.logimarui.auth.infra.persistence.jpa.EmployeeJpaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class EmployeeRepositoryJpa implements EmployeeRepository {

    private final EmployeeJpaRepository jpa;

    @Override
    public boolean isAuthorizedForUserCreation(Long employeeId) {
        return jpa.existsByEmployeeIdAndAuthorizationTrue(employeeId);
    }

    @Override
    public Optional<String> nameByEmployee(Long employeeId) {
        return jpa.findNameByEmployeeId(employeeId);
    }
}
