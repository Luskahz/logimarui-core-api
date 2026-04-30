package com.logimarui.auth.infra.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_employee")
public class EmployeeEntity {

    @Id
    @Column(name = "employee_id")
    private Long employeeId;

    private String name;

    private boolean authorization;
}
