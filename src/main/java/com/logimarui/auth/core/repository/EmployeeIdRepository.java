package com.logimarui.auth.core.repository;

public interface EmployeeIdRepository {

    boolean userCanBeCreated(Long employeeId);
}
