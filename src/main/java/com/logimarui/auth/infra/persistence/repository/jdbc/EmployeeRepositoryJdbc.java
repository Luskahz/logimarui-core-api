package com.logimarui.auth.infra.persistence.repository.jdbc;

import com.logimarui.auth.core.repository.EmployeeIdRepository;
import com.logimarui.auth.infra.persistence.jdbc.EmployeeJdbcRepository;

public class EmployeeRepositoryJdbc implements EmployeeIdRepository {
    EmployeeJdbcRepository jdbc;

    @Override
    public boolean userCanBeCreated(Long employeeId) {
        return false;
    }
}
