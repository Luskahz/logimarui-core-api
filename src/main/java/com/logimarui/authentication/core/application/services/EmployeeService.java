package com.logimarui.authentication.core.application.services;

import com.logimarui.authentication.core.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    EmployeeRepository employeeRepository;

    public String nameFromEmployee(Long employeeId){
        return employeeRepository.nameByEmployee(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not authorized"));
    }
}
